package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.audio.cache.impl.AudioCache;
import com.persybot.cache.CacheAggregator;
import com.persybot.cache.impl.CacheAggregatorImpl;
import com.persybot.command.aggregator.impl.TextCommandAggregator;
import com.persybot.command.impl.commands.PlayMusicCommand;
import com.persybot.config.ConfigReader;
import com.persybot.config.impl.ConfigReaderImpl;
import com.persybot.enums.TEXT_COMMAND;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Bot {
    private static CacheAggregator cacheAggregator;

    private Bot() throws IOException, LoginException {
        ConfigReader config = new ConfigReaderImpl("Main\\src\\main\\resources\\botConfig.cfg");
        String token = config.getProperty("bot.token");
        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(new DefaultListenerAdapter(defaultTextCommandAggregator()))
                .build();

    }

    private TextCommandAggregator defaultTextCommandAggregator() {
        return new TextCommandAggregator.Builder()
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicCommand()).build();
    }


    public static void main(String[] args) throws IOException, LoginException {
        new Bot();
    }

    private void initCache() {
        cacheAggregator = CacheAggregatorImpl.getINSTANCE();

        cacheAggregator.addCache(AudioCache.class, AudioCache.getInstance());
    }







    // TODO: change to cache reader for each channel personality
    public static String DEFAULT_PREFIX = "..";
}
