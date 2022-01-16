package com.persybot.enums;

public enum TEXT_COMMAND {
//    HELP(""),

    // Music
    JOIN("join", "j"),
    LEAVE("leave"),
    PLAY("play", "p"),
    SKIP("skip"),
    STOP("stop", "s"),
    REPEAT("repeat"),
    MIX("mix"),

    PLAYLIST("playlist", "pl"),
    QUEUE("queue"),

    // Channel admin
    PREFIX("prefix"),
    VOLUME("volume", "vol"),
    ADDMEET("addmeet", "am"),
    REMOVEMEET("removemeet", "rm"),
    EQUALIZER("equalizer", "eq");

    private final String[] aliases;

    TEXT_COMMAND(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}
