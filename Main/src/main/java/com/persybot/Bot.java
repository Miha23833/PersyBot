package com.persybot;

import com.persybot.adapters.DefaultListenerAdapter;
import com.persybot.adapters.JDAStateListenerAdapter;
import com.persybot.adapters.SelfMessagesListener;
import com.persybot.adapters.ServiceUpdaterAdapter;
import com.persybot.adapters.perfomance.VoiceInactivityChecker;
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
import com.persybot.command.impl.commands.EqualizerTextCommand;
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
import com.persybot.config.impl.ConfigFileReader;
import com.persybot.config.impl.EnvironmentVariableReader;
import com.persybot.config.impl.MasterConfigImpl;
import com.persybot.config.pojo.BotConfig;
import com.persybot.config.pojo.DBConfig;
import com.persybot.db.refdata.loader.RefDataLoaderFactory;
import com.persybot.db.refdata.loader.impl.EqualizerPresetXmlLoader;
import com.persybot.db.service.DBService;
import com.persybot.db.service.HibernateDBService;
import com.persybot.enums.BUTTON_ID;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.spotify.api.SpotifyApiDataService;
import com.persybot.spotify.api.SpotifyApiDataServiceImpl;
import com.persybot.youtube.api.YoutubeApiDataService;
import com.persybot.youtube.api.YoutubeApiDataServiceImpl;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Bot {
    private Bot(DBConfig dbConfig, BotConfig botConfig) {
        try {
            populateServicesBeforeLaunch(dbConfig, botConfig);
            loadRefData(botConfig);
            DefaultShardManagerBuilder.createDefault(botConfig.discordToken)
                    .addEventListeners(
                            new DefaultListenerAdapter(defaultTextCommandAggregator(botConfig), defaultButtonCommandAggregator()),
                            new ServiceUpdaterAdapter(botConfig),
                            new SelfMessagesListener(),
                            new JDAStateListenerAdapter(),
                            new VoiceInactivityChecker(botConfig))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MODERATION,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGE_TYPING,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (Throwable e) {
            PersyBotLogger.BOT_LOGGER.fatal(e.getStackTrace(), e);
        }
    }

    private TextCommandServiceImpl defaultTextCommandAggregator(BotConfig botConfig) {
        return new TextCommandServiceImpl()
                .addCommand(TEXT_COMMAND.JOIN, new JoinToVoiceChannelCommand())
                .addCommand(TEXT_COMMAND.PLAY, new PlayMusicTextCommand())
                .addCommand(TEXT_COMMAND.SKIP, new SkipSongTextCommand())
                .addCommand(TEXT_COMMAND.VOLUME, new SetVolumeTextCommand())
                .addCommand(TEXT_COMMAND.LEAVE, new LeaveChannelTextCommand())
                .addCommand(TEXT_COMMAND.STOP, new StopPlayingTextCommand())
                .addCommand(TEXT_COMMAND.PREFIX, new ChangePrefixCommand(botConfig))
                .addCommand(TEXT_COMMAND.REPEAT, new RepeatSongTextCommand())
                .addCommand(TEXT_COMMAND.MIX, new MixPlayingTracksCommand())
                .addCommand(TEXT_COMMAND.PLAYLIST, new PlaylistCommand(10))
                .addCommand(TEXT_COMMAND.ADDMEET, new AddMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.REMOVEMEET, new RemoveMeetSoundTextCommand())
                .addCommand(TEXT_COMMAND.QUEUE, new ShowQueueCommand())
                .addCommand(TEXT_COMMAND.EQUALIZER, new EqualizerTextCommand());
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

    private void populateServicesBeforeLaunch(DBConfig dbConfig, BotConfig botConfig) throws GeneralSecurityException, IOException {
        ServiceAggregator.getInstance()
                .add(DBService.class, new HibernateDBService(dbConfig))
                .add(CacheService.class, createCacheService())
                .add(YoutubeApiDataService.class, new YoutubeApiDataServiceImpl(botConfig.youtubeApiKey))
                .add(SpotifyApiDataService.class, new SpotifyApiDataServiceImpl(botConfig.spotifyClientId, botConfig.spotifyClientSecret))
                .add(ChannelService.class, ChannelServiceImpl.getInstance(botConfig));
    }

    private void loadRefData(BotConfig botConfig) {
        new RefDataLoaderFactory()
                .addLoader(new EqualizerPresetXmlLoader(ServiceAggregator.getInstance().get(DBService.class), botConfig.refDataPath + "EqualizerPresets.xml"))
                .process();
    }

    private static DBConfig getDbConfig() {
        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("hibernate.config_path", "HIBERNATE_CONFIG_PATH")
                .requireProperty("hibernate.connection.url", "HIBERNATE_CONN_URL")
                .requireProperty("hibernate.connection.username", "HIBERNATE_CONN_USERNAME")
                .requireProperty("hibernate.connection.password", "HIBERNATE_CONN_PWD");

        return new DBConfig(new MasterConfigImpl()
                .addConfigSource(envConfig)
                .getProperties());
    }

    private static BotConfig getBotConfig() {
        ConfigFileReader fileConfig = new ConfigFileReader("resources/botConfig.cfg");

        EnvironmentVariableReader envConfig = new EnvironmentVariableReader()
                .requireProperty("bot.token", "BOT_TOKEN")
                .requireProperty("spotify.client_id", "SPOTIFY_CLIENT_ID")
                .requireProperty("spotify.client_secret", "SPOTIFY_CLIENT_SECRET")
                .requireProperty("youtube.api_key", "YOUTUBE_API_KEY");

        return new BotConfig(new MasterConfigImpl()
                .addConfigSource(fileConfig)
                .addConfigSource(envConfig)
                .getProperties()
        );
    }

    private static CacheService createCacheService() {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.add(PageableMessageCache.class, new PageableMessageCache());
        return cacheService;
    }

    public static void main(String[] args) {
        new Bot(getDbConfig(), getBotConfig());
    }
}
