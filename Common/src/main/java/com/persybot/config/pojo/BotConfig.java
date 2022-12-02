package com.persybot.config.pojo;

import java.util.Properties;

public class BotConfig {
    public final int selfMessagesLimit;

    public final String defaultPrefix;
    public final byte maxPrefixLen;

    public final long activityCheckPauseMillis;
    public final long maxInactivityTimeMillis;

    public final String discordToken;

    public final String refDataPath;

    public final String youtubeApiKey;
    public final String spotifyClientId;
    public final String spotifyClientSecret;

    public BotConfig(Properties properties) {
        this.selfMessagesLimit = Integer.parseInt(properties.getProperty("bot.self_messages.limit"));
        this.defaultPrefix = properties.getProperty("bot.prefix.default");
        this.maxPrefixLen = Byte.parseByte(properties.getProperty("bot.prefix.max_length"));
        this.activityCheckPauseMillis =Long.parseLong(properties.getProperty("bot.activitychecher.check_pause"));
        this.maxInactivityTimeMillis = Long.parseLong(properties.getProperty("bot.activitychecher.max_inactivity_time_millis"));
        this.discordToken = properties.getProperty("bot.token");
        this.refDataPath = properties.getProperty("refdata_path");
        this.youtubeApiKey = properties.getProperty("youtube.api_key");
        this.spotifyClientId = properties.getProperty("spotify.client_id");
        this.spotifyClientSecret = properties.getProperty("spotify.client_secret");
    }
}
