package com.persybot.adapters;

import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.service.impl.SelfFloodControllerImpl;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JDAStateListenerAdapter extends ListenerAdapter {
    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        PersyBotLogger.BOT_LOGGER.info("Bot status was changed from " + event.getOldStatus() + " to " + event.getNewStatus());

        if (event.getNewStatus().equals(JDA.Status.INITIALIZED) ) {
            populateServices(event.getEntity());
        }
    }

    private void populateServices(JDA jda) {
        ServiceAggregator.getInstance().add(SelfFloodController.class, new SelfFloodControllerImpl(jda));
    }
}
