package com.persybot.db.common.constraint.impl;

import com.persybot.db.common.AbstractOperationResult;

public class ValidateConstraintResult extends AbstractOperationResult {
    private ValidateConstraintResult() {
        super();
    }

    public static ValidateConstraintResult getDefaultValid() {
        return new ValidateConstraintResult();
    }
}
