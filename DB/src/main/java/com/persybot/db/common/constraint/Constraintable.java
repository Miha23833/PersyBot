package com.persybot.db.common.constraint;

import com.persybot.db.common.constraint.impl.ValidateConstraintResult;
import com.persybot.db.common.OperationResult;

public interface Constraintable {
    default OperationResult validate() {
        return ValidateConstraintResult.getDefaultValid();
    }
}
