package com.persybot.enums;

public enum TEXT_COMMAND_REJECT_REASON {
    NOT_ENOUGH_ARGS("Not enough arguments."),

    OTHER("Not described.");

    private String reasonText;

    TEXT_COMMAND_REJECT_REASON(String reasonText) {
        this.reasonText = reasonText;
    }

    public String text() {
        return reasonText;
    }
}
