package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.adapters.JDAStateListenerAdapter;
import com.persybot.adapters.SelfMessagesListener;
import com.persybot.adapters.ServiceUpdaterAdapter;
import com.persybot.channel.service.ChannelService;
import com.persybot.channel.service.impl.ChannelServiceImpl;
import com.persybot.command.button.impl.commands.PauseButtonCommand;
import com.persybot.command.button.impl.commands.ResumeButtonCommand;
import com.persybot.command.button.impl.commands.SkipSongButtonCommand;
import com.persybot.command.button.impl.commands.StopPlayingButtonCommand;
import com.persybot.command.impl.commands.ChangePrefixCommand;
import com.persybot.command.impl.commands.JoinToVoiceChannelCommand;
import com.persybot.command.impl.commands.LeaveChannelTextCommand;
import com.persybot.command.impl.commands.MixPlayingTracksCommand;
import com.persybot.command.impl.commands.PlayMusicTextCommand;
import com.persybot.command.impl.commands.RepeatSongTextCommand;
import com.persybot.command.impl.commands.SetVolumeTextCommand;
import com.persybot.command.impl.commands.SkipSongTextCommand;
import com.persybot.command.impl.commands.StopPlayingTextCommand;
import com.persybot.command.service.TextCommandService;
import com.persybot.command.service.impl.ButtonCommandServiceImpl;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.config.MasterConfig;
import com.persybot.config.impl.ConfigFileReader;
import com.persybot.config.impl.EnvironmentVariableReader;
import com.persybot.config.impl.MasterConfigImpl;
import com.persybot.db.service.DBService;
import com.persybot.db.service.DBServiceImpl;
import com.persybot.enums.BUTTON_ID;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.message.service.MessageAggregatorService;
import com.persybot.message.service.impl.MessageAggregatorServiceImpl;
import com.persybot.service.ServiceAggregator;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.StaticDataImpl;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.security.auth.login.LoginException;
import java.util.Properties;

public class Bot {
    private Bot(Properties dbProperties, Properties botProperties) throws LoginException {
        populateServices(dbProperties, botProperties);
        DefaultShardManagerBuilder.createDefault(botProperties.getProperty("bot.token"))
                .addEventListeners(new DefaultListenerAdapter(defaultTextCommandAggregator(botProperties),
                        defaultButtonCommandAggregator(botProperties)),
                        new ServiceUpdaterAdapter(),
                        new SelfMessagesListener(Integer.parseInt(botProperties.getProperty("bot.selfmessageslimit"))),
                        new JDAStateListenerAdapter(botProperties))
                .build();
    }

    private TextCommandServiceImpl defaultTextCommandAggregator(Properties properties) {
        return TextCommandServiceImpl.getInstance()
                .addCommand(TEXT_COMMAND.JOIN, new JoinToVoiceChannelCommand())
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicTextCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongTextCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeTextCommand())
                .addCommand(TEXT_COMMAND.LEAVE, new LeaveChannelTextCommand())
                .addCommand(TEXT_COMMAND.STOP, new StopPlayingTextCommand())
                .addCommand(TEXT_COMMAND.PREFIX, new ChangePrefixCommand(Integer.parseInt(properties.getProperty("bot.prefix.maxlen"))))
                .addCommand(TEXT_COMMAND.REPEAT, new RepeatSongTextCommand())
                .addCommand(TEXT_COMMAND.MIX, new MixPlayingTracksCommand());
    }

    private ButtonCommandServiceImpl defaultButtonCommandAggregator(Properties properties) {
        return ButtonCommandServiceImpl.getInstance()
                .addCommand(BUTTON_ID.PLAYER_PAUSE, new PauseButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_RESUME, new ResumeButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_SKIP, new SkipSongButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_STOP, new StopPlayingButtonCommand());
    }

    private void populateServices(Properties dbProperties, Properties botProperties) {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance()
                .addService(MessageAggregatorService.class, MessageAggregatorServiceImpl.getInstance())
                .addService(DBService.class, DBServiceImpl.getInstance(dbProperties))
                .addService(TextCommandService.class, defaultTextCommandAggregator(botProperties))
                .addService(ChannelService.class, ChannelServiceImpl.getInstance())
                .addService(StaticData.class, StaticDataImpl.getInstance());
        serviceAggregator.getService(DBService.class).start();
    }


    public static void main(String[] args) throws LoginException {
        new Bot(getDbProperties(), getBotProperties());
    }

    private static Properties getDbProperties() {
        ConfigFileReader fileConfig = new ConfigFileReader("resources/hibernateConfig.cfg");

        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("db.workers.count")
                .requireProperty("DATABASE_URL")
                .requireProperty("hibernate.connection.driver_class")
                .requireProperty("hibernate.show_sql")
                .requireProperty("hibernate.connection.url")
                .requireProperty("hibernate.connection.username")
                .requireProperty("hibernate.connection.password")
                .requireProperty("hibernate.connection.charSet")
                .requireProperty("hibernate.connection.characterEncoding")
                .requireProperty("hibernate.connection.useUnicode")
                .requireProperty("hibernate.connection.pool_size")
                .requireProperty("hibernate.dialect")
                .requireProperty("hibernate.hbm2ddl.auto")
                .requireProperty("hibernate.current_session_context_class");

        MasterConfig dbConfig = new MasterConfigImpl();
        return dbConfig
                .addConfigSource(fileConfig)
                .addConfigSource(envConfig)
                .getProperties();
    }

    private static Properties getBotProperties() {
        ConfigFileReader fileConfig = new ConfigFileReader("resources/botConfig.cfg");

        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("bot.activityChecker.checkPause")
                .requireProperty("bot.activityChecker.maxInactivityTime")
                .requireProperty("bot.token")
                .requireProperty("bot.selfmessageslimit")
                .requireProperty("bot.prefix.maxlen");

        MasterConfig botConfig = new MasterConfigImpl();
        return botConfig
                .addConfigSource(fileConfig)
                .addConfigSource(envConfig)
                .getProperties();
    }
}
