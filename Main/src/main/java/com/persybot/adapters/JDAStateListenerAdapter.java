package com.persybot.adapters;

import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.service.impl.SelfFloodControllerImpl;
import com.persybot.perfomance.voiceactivity.impl.VoiceInactivityChecker;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.staticdata.StaticData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;

public class JDAStateListenerAdapter extends ListenerAdapter {
    private final Properties botProperties;

    public JDAStateListenerAdapter(Properties botProperties) {this.botProperties = botProperties;}


    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        PersyBotLogger.BOT_LOGGER.info("Bot status was changed from " + event.getOldStatus() + " to " + event.getNewStatus());

        if (event.getNewStatus().equals(JDA.Status.INITIALIZED) ) {
            populateServices(botProperties, event.getEntity());

            runVoiceActivityChecker(this.botProperties);
        }
    }

    private void runVoiceActivityChecker(Properties botProperties) {
        long checkPause = Long.parseLong(botProperties.getProperty("BOT_ACTIVITY_CHECKER_CHECK_PAUSE"));
        Map<Long, Long> activeChannels = ServiceAggregator.getInstance().get(StaticData.class).getGuildsWithActiveVoiceChannel();

        long maxInactivityTime = Long.parseLong(botProperties.getProperty("BOT_ACTIVITY_CHECKER_MAX_INACTIVITY_TIME"));

        VoiceInactivityChecker activityChecker = new VoiceInactivityChecker(activeChannels, checkPause, maxInactivityTime);

        activityChecker.run();
    }

    private void populateServices(Properties botProperties, JDA jda) {
        ServiceAggregator.getInstance().add(SelfFloodController.class, new SelfFloodControllerImpl(jda));
    }
}
