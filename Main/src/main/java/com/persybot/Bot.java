package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.adapters.JDAStateListenerAdapter;
import com.persybot.adapters.SelfMessagesListener;
import com.persybot.adapters.ServiceUpdaterAdapter;
import com.persybot.cache.service.CacheService;
import com.persybot.cache.service.CacheServiceImpl;
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
import com.persybot.command.service.impl.ButtonCommandContainerImpl;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.config.MasterConfig;
import com.persybot.config.impl.ConfigFileReader;
import com.persybot.config.impl.EnvironmentVariableReader;
import com.persybot.config.impl.MasterConfigImpl;
import com.persybot.db.service.DBService;
import com.persybot.db.service.HibernateDBService;
import com.persybot.enums.BUTTON_ID;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.StaticDataImpl;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import java.util.Properties;

public class Bot {
    private Bot(Properties dbProperties, Properties botProperties) {
        try {
            populateServicesBeforeLaunch(dbProperties);
            DefaultShardManagerBuilder.createDefault(botProperties.getProperty("BOT_TOKEN"))
                    .addEventListeners(
                            new DefaultListenerAdapter(defaultTextCommandAggregator(botProperties), defaultButtonCommandAggregator()),
                            new ServiceUpdaterAdapter(botProperties),
                            new SelfMessagesListener(),
                            new JDAStateListenerAdapter(botProperties))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_BANS,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGE_TYPING,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING)
                    .build();
        } catch (Throwable e) {
            PersyBotLogger.BOT_LOGGER.fatal(e.getStackTrace(), e);
        }
    }

    private TextCommandServiceImpl defaultTextCommandAggregator(Properties properties) {
        return new TextCommandServiceImpl()
                .addCommand(TEXT_COMMAND.JOIN, new JoinToVoiceChannelCommand())
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicTextCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongTextCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeTextCommand())
                .addCommand(TEXT_COMMAND.LEAVE, new LeaveChannelTextCommand())
                .addCommand(TEXT_COMMAND.STOP, new StopPlayingTextCommand())
                .addCommand(TEXT_COMMAND.PREFIX, new ChangePrefixCommand(Integer.parseInt(properties.getProperty("BOT_PREFIX_MAXLEN"))))
                .addCommand(TEXT_COMMAND.REPEAT, new RepeatSongTextCommand())
                .addCommand(TEXT_COMMAND.MIX, new MixPlayingTracksCommand())
                .addCommand(TEXT_COMMAND.PLAYLIST, new PlaylistCommand(10))
                .addCommand(TEXT_COMMAND.ADDMEET, new AddMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.REMOVEMEET, new RemoveMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.QUEUE, new ShowQueueCommand())
//                .addCommand(TEXT_COMMAND.EQUALIZER, new EqualizerTextCommand())
                ;
    }

    private ButtonCommandContainerImpl defaultButtonCommandAggregator() {
        return new ButtonCommandContainerImpl()
                .addCommand(BUTTON_ID.PLAYER_PAUSE, new PauseButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_RESUME, new ResumeButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_SKIP, new SkipSongButtonCommand())
                .addCommand(BUTTON_ID.PLAYER_STOP, new StopPlayingButtonCommand())
                .addCommand(BUTTON_ID.PREV_PAGE, new PrevPageCommand())
                .addCommand(BUTTON_ID.NEXT_PAGE, new NextPageCommand());
    }

    private void populateServicesBeforeLaunch(Properties dbProperties) {
        ServiceAggregator.getInstance()
                .add(DBService.class, new HibernateDBService(dbProperties))
                .add(CacheService.class, createCacheService())
                .add(StaticData.class, new StaticDataImpl())
                .add(ChannelService.class, ChannelServiceImpl.getInstance());
    }

    private static Properties getDbProperties() {
        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("HIBERNATE_CONFIG_PATH")
                .requireProperty("DB_URL")
                .requireProperty("CHARACTER_ENCODING")
                .requireProperty("DB_USERNAME")
                .requireProperty("DB_PASSWORD")
                .requireProperty("SQL_XML_PATH")
                .requireProperty("SQL_FILE_DIR");

        MasterConfig dbConfig = new MasterConfigImpl();
        return dbConfig
                .addConfigSource(envConfig)
                .getProperties();
    }

    private static Properties getBotProperties() {
        ConfigFileReader fileConfig = new ConfigFileReader("resources/botConfig.cfg");

        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("BOT_ACTIVITY_CHECKER_CHECK_PAUSE")
                .requireProperty("BOT_ACTIVITY_CHECKER_MAX_INACTIVITY_TIME")
                .requireProperty("BOT_TOKEN")
                .requireProperty("BOT_SELF_MESSAGES_LIMIT")
                .requireProperty("BOT_PREFIX_DEFAULT")
                .requireProperty("BOT_PREFIX_MAXLEN");

        MasterConfig botConfig = new MasterConfigImpl();
        return botConfig
                .addConfigSource(fileConfig)
                .addConfigSource(envConfig)
                .getProperties();
    }

    private static CacheService createCacheService() {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.add(PageableMessageCache.class, new PageableMessageCache());
        return cacheService;
    }

    public static void main(String[] args) {
        new Bot(getDbProperties(), getBotProperties());
    }
}
