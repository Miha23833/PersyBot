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
import java.util.Properties;

public class Bot {
    private Bot(Properties properties) throws IOException, LoginException {
        populateServices(properties);
        ShardManager jda = DefaultShardManagerBuilder.createDefault(properties.getProperty("bot.token"))
                .addEventListeners(new DefaultListenerAdapter(defaultTextCommandAggregator(properties),
                        defaultButtonCommandAggregator(properties)),
                        new ServiceUpdaterAdapter(),
                        new SelfMessagesCleaner(Integer.parseInt(properties.getProperty("bot.selfmessageslimit", "100"))))
                .build();
    }

    private TextCommandServiceImpl defaultTextCommandAggregator(Properties properties) {
        return TextCommandServiceImpl.getInstance()
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicTextCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongTextCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeTextCommand())
                .addCommand(TEXT_COMMAND.LEAVE, new LeaveChannelTextCommand())
                .addCommand(TEXT_COMMAND.STOP, new StopPlayingTextCommand())
                .addCommand(TEXT_COMMAND.PREFIX, new ChangePrefixCommand(Integer.parseInt(properties.getProperty("bot.prefix.maxlen", "3"))));
    }

    private ButtonCommandServiceImpl defaultButtonCommandAggregator(Properties properties) {
        return ButtonCommandServiceImpl.getInstance()
                .addCommand(BUTTON_ID.PLAYER_PAUSE, new PauseButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_RESUME, new ResumeButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_SKIP, new SkipSongButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_STOP, new StopPlayingButtonCommand());
    }

    private void populateServices(Properties properties) {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance()
                .addService(DBService.class, DBServiceImpl.getInstance(properties))
                .addService(TextCommandService.class, defaultTextCommandAggregator(properties))
                .addService(ChannelService.class, ChannelServiceImpl.getInstance());
        serviceAggregator.getService(DBService.class).start();
    }


    public static void main(String[] args) throws IOException, LoginException {
        Properties properties = new Properties();

        properties.put("bot.selfmessageslimit", 50);
        properties.put("db.workers.count", 20);
        properties.put("bot.prefix.maxlen", 3);
        properties.put("bot.token", System.getenv("bot.token"));
        properties.put("hibernate.connection.url", System.getenv("JDBC_DATABASE_URL"));

        new Bot(properties);
    }
}
