package com.persybot.validation.impl;

import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.validation.ValidationResult;

public class TextCommandValidationResult implements ValidationResult<TEXT_COMMAND_REJECT_REASON> {

    private String rejectionReasonText = null;
    private TEXT_COMMAND_REJECT_REASON rejectReason;

    @Override
    public boolean isValid() {
        return rejectReason == null;
    }

    @Override
    public String rejectText() {
        if (rejectReason == null) {
            return rejectionReasonText;
        }
        return rejectReason.text() + rejectionReasonText;
    }

    @Override
    public void setInvalid(TEXT_COMMAND_REJECT_REASON rejectReason, String rejectReasonText) {
        this.rejectReason = rejectReason;
        this.rejectionReasonText = rejectReasonText;
    }

    @Override
    public TEXT_COMMAND_REJECT_REASON getRejectReason() {
        return rejectReason;
    }
}
