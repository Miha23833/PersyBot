package com.persybot.adapters;

import com.persybot.config.pojo.BotConfig;
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

public class JDAStateListenerAdapter extends ListenerAdapter {
    private final BotConfig botConfig;

    public JDAStateListenerAdapter(BotConfig botConfig) {this.botConfig = botConfig;}


    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        PersyBotLogger.BOT_LOGGER.info("Bot status was changed from " + event.getOldStatus() + " to " + event.getNewStatus());

        if (event.getNewStatus().equals(JDA.Status.INITIALIZED) ) {
            populateServices(event.getEntity());

            runVoiceActivityChecker();
        }
    }

    private void runVoiceActivityChecker() {
        long checkPause = botConfig.activityCheckPauseMillis;
        Map<Long, Long> activeChannels = ServiceAggregator.getInstance().get(StaticData.class).getGuildsWithActiveVoiceChannel();

        long maxInactivityTime = botConfig.maxInactivityTimeMillis;

        VoiceInactivityChecker activityChecker = new VoiceInactivityChecker(activeChannels, checkPause, maxInactivityTime);

        activityChecker.run();
    }

    private void populateServices(JDA jda) {
        ServiceAggregator.getInstance().add(SelfFloodController.class, new SelfFloodControllerImpl(jda));
    }
}
