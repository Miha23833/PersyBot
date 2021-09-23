package com.persybot.db.common;

public interface OperationResult {
    boolean isValid();

    String getFailDescription();

    void addInvalidCause(String message);
}
