package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.adapters.SelfMessagesCleaner;
import com.persybot.adapters.ServiceUpdaterAdapter;
import com.persybot.channel.service.ChannelService;
import com.persybot.channel.service.impl.ChannelServiceImpl;
import com.persybot.command.button.impl.commands.PauseButtonCommand;
import com.persybot.command.button.impl.commands.ResumeButtonCommand;
import com.persybot.command.button.impl.commands.SkipSongButtonCommand;
import com.persybot.command.button.impl.commands.StopPlayingButtonCommand;
import com.persybot.command.impl.commands.ChangePrefixCommand;
import com.persybot.command.impl.commands.LeaveChannelTextCommand;
import com.persybot.command.impl.commands.PlayMusicTextCommand;
import com.persybot.command.impl.commands.SetVolumeTextCommand;
import com.persybot.command.impl.commands.SkipSongTextCommand;
import com.persybot.command.impl.commands.StopPlayingTextCommand;
import com.persybot.command.service.TextCommandService;
import com.persybot.command.service.impl.ButtonCommandServiceImpl;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.config.ConfigReader;
import com.persybot.config.impl.ConfigReaderImpl;
import com.persybot.db.service.DBService;
import com.persybot.db.service.DBServiceImpl;
import com.persybot.enums.BUTTON_ID;
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
                .addEventListeners(new DefaultListenerAdapter(defaultTextCommandAggregator(),
                        defaultButtonCommandAggregator()),
                        new ServiceUpdaterAdapter(),
                        // TODO: move limit to config file
                        new SelfMessagesCleaner(50))
                .build();
    }

    private TextCommandServiceImpl defaultTextCommandAggregator() {
        return TextCommandServiceImpl.getInstance()
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicTextCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongTextCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeTextCommand())
                .addCommand(TEXT_COMMAND.LEAVE, new LeaveChannelTextCommand())
                .addCommand(TEXT_COMMAND.STOP, new StopPlayingTextCommand())
                //TODO remove hardcode of maxPrefixLen
                .addCommand(TEXT_COMMAND.PREFIX, new ChangePrefixCommand(3));
    }

    private ButtonCommandServiceImpl defaultButtonCommandAggregator() {
        return ButtonCommandServiceImpl.getInstance()
                .addCommand(BUTTON_ID.PLAYER_PAUSE, new PauseButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_RESUME, new ResumeButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_SKIP, new SkipSongButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_STOP, new StopPlayingButtonCommand());
    }


    public static void main(String[] args) throws IOException, LoginException {
        new Bot();
    }

    private void populateServices() {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance()
                .addService(DBService.class, DBServiceImpl.getInstance(5))
                .addService(TextCommandService.class, defaultTextCommandAggregator())
                .addService(ChannelService.class, ChannelServiceImpl.getInstance());
        serviceAggregator.getService(DBService.class).start();
    }
}
