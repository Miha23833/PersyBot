package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.adapters.JDAStateListenerAdapter;
import com.persybot.adapters.SelfMessagesListener;
import com.persybot.adapters.ServiceUpdaterAdapter;
import com.persybot.channel.service.ChannelService;
import com.persybot.channel.service.impl.ChannelServiceImpl;
import com.persybot.command.button.impl.commands.NextPageCommand;
import com.persybot.command.button.impl.commands.PauseButtonCommand;
import com.persybot.command.button.impl.commands.PrevPageCommand;
import com.persybot.command.button.impl.commands.ResumeButtonCommand;
import com.persybot.command.button.impl.commands.SkipSongButtonCommand;
import com.persybot.command.button.impl.commands.StopPlayingButtonCommand;
import com.persybot.command.impl.commands.AddMeetSoundTextCommand;
import com.persybot.command.impl.commands.ChangePrefixCommand;
import com.persybot.command.impl.commands.JoinToVoiceChannelCommand;
import com.persybot.command.impl.commands.LeaveChannelTextCommand;
import com.persybot.command.impl.commands.MixPlayingTracksCommand;
import com.persybot.command.impl.commands.PlayMusicTextCommand;
import com.persybot.command.impl.commands.PlaylistCommand;
import com.persybot.command.impl.commands.RemoveMeetSoundTextCommand;
import com.persybot.command.impl.commands.RepeatSongTextCommand;
import com.persybot.command.impl.commands.SetVolumeTextCommand;
import com.persybot.command.impl.commands.ShowQueueCommand;
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
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageAggregatorService;
import com.persybot.message.service.impl.MessageAggregatorServiceImpl;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.StaticDataImpl;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Bot {
    private Bot(Properties dbProperties, Properties botProperties) {
        try {
            
            populateServicesBeforeLaunch(dbProperties, botProperties);
            DefaultShardManagerBuilder.createDefault(botProperties.getProperty("bot.token"))
                    .addEventListeners(
                            new DefaultListenerAdapter(defaultTextCommandAggregator(botProperties), defaultButtonCommandAggregator()),
                            new ServiceUpdaterAdapter(botProperties),
                            new SelfMessagesListener(Integer.parseInt(botProperties.getProperty("bot.selfmessageslimit"))),
                            new JDAStateListenerAdapter(botProperties))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
        } catch (Throwable e) {
            PersyBotLogger.BOT_LOGGER.fatal(e.getStackTrace(), e);
        }
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
                .addCommand(TEXT_COMMAND.MIX, new MixPlayingTracksCommand())
                .addCommand(TEXT_COMMAND.PLAYLIST, new PlaylistCommand(10))
                .addCommand(TEXT_COMMAND.ADDMEET, new AddMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.REMOVEMEET, new RemoveMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.QUEUE, new ShowQueueCommand());
    }

    private ButtonCommandServiceImpl defaultButtonCommandAggregator() {
        return ButtonCommandServiceImpl.getInstance()
                .addCommand(BUTTON_ID.PLAYER_PAUSE, new PauseButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_RESUME, new ResumeButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_SKIP, new SkipSongButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_STOP, new StopPlayingButtonCommand())
                .addCommand(BUTTON_ID.PREV_PAGE, new PrevPageCommand())
                .addCommand(BUTTON_ID.NEXT_PAGE, new NextPageCommand());
    }

    private void populateServicesBeforeLaunch(Properties dbProperties, Properties botProperties) throws SQLException, IOException, SAXException, ParserConfigurationException {
        ServiceAggregatorImpl.getInstance()
                .addService(MessageAggregatorService.class, MessageAggregatorServiceImpl.getInstance())
                .addService(DBService.class, DBServiceImpl.getInstance(dbProperties))
                .addService(StaticData.class, StaticDataImpl.getInstance())
                .addService(TextCommandService.class, defaultTextCommandAggregator(botProperties))
                .addService(ChannelService.class, ChannelServiceImpl.getInstance());
    }

    private static Properties getDbProperties() {
        ConfigFileReader fileConfig = new ConfigFileReader("resources/dbConfig.cfg");

        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("db.url")
                .requireProperty("characterEncoding")
                .requireProperty("db.username")
                .requireProperty("db.password")
                .requireProperty("db.query.source.SqlXmlPath")
                .requireProperty("db.query.source.sqlFileDir");

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

    public static void main(String[] args) {
        new Bot(getDbProperties(), getBotProperties());
    }
}
