package com.persybot.enums;

public enum TEXT_COMMAND {
//    HELP(""),

    // Music
//    JOIN(""),
    LEAVE(""),
    PLAY("Plays music. To use write '''<prefix>play <link or name of sound>'''."),
    SKIP(""),
    STOP(""),
    REPEAT(""),


    // Channel admin
    PREFIX(""),
    VOLUME(""),

    TEST("");

    private final String describeText;

    TEXT_COMMAND(String describeText) {
        this.describeText = describeText;
    }

    public String describeText() {
        return describeText;
    }
}
