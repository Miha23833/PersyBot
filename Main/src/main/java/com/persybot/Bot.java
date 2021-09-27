package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.audio.PlayerManagerService;
import com.persybot.audio.impl.PlayerManagerServiceImpl;
import com.persybot.command.impl.commands.PlayMusicCommand;
import com.persybot.command.impl.commands.SetVolumeCommand;
import com.persybot.command.impl.commands.SkipSongCommand;
import com.persybot.command.service.TextCommandService;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.config.ConfigReader;
import com.persybot.config.impl.ConfigReaderImpl;
import com.persybot.db.service.DBService;
import com.persybot.db.service.DBServiceImpl;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.service.ServiceAggregator;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Bot {
    private Bot() throws IOException, LoginException {
        ConfigReader config = new ConfigReaderImpl("Main\\src\\main\\resources\\botConfig.cfg");
        String token = config.getProperty("bot.token");
        populateServices();
        ShardManager jda = DefaultShardManagerBuilder.createDefault(token)
                .addEventListeners(new DefaultListenerAdapter(defaultTextCommandAggregator()))
                .build();
    }

    private TextCommandServiceImpl defaultTextCommandAggregator() {
        return TextCommandServiceImpl.getInstance()
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeCommand());
    }


    public static void main(String[] args) throws IOException, LoginException {
        new Bot();
    }

    private void populateServices() {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance()
                .addService(DBService.class, DBServiceImpl.getInstance(5))
                .addService(PlayerManagerService.class, PlayerManagerServiceImpl.getInstance())
                .addService(TextCommandService.class, defaultTextCommandAggregator());
        serviceAggregator.getService(DBService.class).start();

    }

    // TODO: change to cache reader for each channel personality
    public static String DEFAULT_PREFIX = "..";
}
