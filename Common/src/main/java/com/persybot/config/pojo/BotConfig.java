package com.persybot.config.pojo;

import java.util.Properties;

public class BotConfig {
    public final int selfMessagesLimit;

    public final String defaultPrefix;
    public final byte maxPrefixLen;

    public final long activityCheckPauseMillis;
    public final long maxInactivityTimeMillis;

    public final String discordToken;

    public BotConfig(Properties properties) {
        this.selfMessagesLimit = Integer.parseInt(properties.getProperty("BOT_SELF_MESSAGES_LIMIT"));
        this.defaultPrefix = properties.getProperty("BOT_PREFIX_DEFAULT");
        this.maxPrefixLen = Byte.parseByte(properties.getProperty("BOT_PREFIX_MAXLEN"));
        this.activityCheckPauseMillis =Long.parseLong(properties.getProperty("BOT_ACTIVITY_CHECKER_CHECK_PAUSE"));
        this.maxInactivityTimeMillis = Long.parseLong(properties.getProperty("BOT_ACTIVITY_CHECKER_MAX_INACTIVITY_TIME"));
        this.discordToken = properties.getProperty("BOT_TOKEN");
    }
}
