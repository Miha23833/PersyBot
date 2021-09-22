package com.persybot.validation;

public interface ValidationResult<T> {
    boolean isValid();

    String rejectionReasonText();

    void setInvalid(T rejectReason, String rejectReasonText);

    T getRejectReason();
}
