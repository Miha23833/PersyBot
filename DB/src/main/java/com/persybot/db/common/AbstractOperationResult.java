package com.persybot.db.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOperationResult implements OperationResult {
    protected boolean isValid;
    protected final List<String> failCauses;

    protected AbstractOperationResult() {
        this.isValid = true;
        this.failCauses = new ArrayList<>();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String getFailDescription() {
        return String.join("\n", failCauses);
    }

    @Override
    public void addInvalidCause(String message) {
        this.failCauses.add(message);
        this.isValid = false;
    }
}
