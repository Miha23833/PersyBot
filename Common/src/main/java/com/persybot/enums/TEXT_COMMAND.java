package com.persybot.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum TEXT_COMMAND {
    HELP(""),

    // Music
    JOIN(""),
    LEAVE(""),
    PLAY("Plays music. To use write '''<prefix>play <link or name of sound>'''."),
    SKIP(""),
    STOP(""),


    // Channel admin
    PREFIX(""),
    VOLUME("");

    private final String describeText;

    TEXT_COMMAND(String describeText) {
        this.describeText = describeText;
    }

    public String describeText() {
        return describeText;
    }
}
