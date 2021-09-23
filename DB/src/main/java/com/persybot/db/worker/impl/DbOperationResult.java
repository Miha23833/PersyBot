package com.persybot.db.worker.impl;

import com.persybot.db.common.AbstractOperationResult;

import java.util.List;

public class DbOperationResult extends AbstractOperationResult {
    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public String getFailDescription() {
        return null;
    }

    @Override
    public void addInvalidCause(String message) {

    }

    public DbOperationResult() {
        super();
    }

    public static DbOperationResult getDefaultValid() {
        return new DbOperationResult();
    }
}
