package com.persybot.validation;

public interface ValidationResult<T> {
    boolean isValid();

    String rejectText();

    void setInvalid(T rejectReason, String rejectReasonText);

    T getRejectReason();
}
