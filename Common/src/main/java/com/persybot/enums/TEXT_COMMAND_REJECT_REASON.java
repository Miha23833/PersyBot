package com.persybot.enums;

public enum TEXT_COMMAND_REJECT_REASON {
    NOT_ENOUGH_ARGS("Not enough arguments"),

    WRONG_VALUE("Wrong argument value"),

    OTHER("Command rejected");

    private String reasonText;

    TEXT_COMMAND_REJECT_REASON(String reasonText) {
        this.reasonText = reasonText;
    }

    public String text() {
        return reasonText;
    }
}
