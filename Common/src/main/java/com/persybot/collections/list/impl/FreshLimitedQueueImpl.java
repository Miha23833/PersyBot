package com.persybot.collections.list.impl;

import com.persybot.collections.list.FreshLimitedQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FreshLimitedQueueImpl<T> implements FreshLimitedQueue<T> {

    private final Queue<T> fresh;
    private final Queue<T> old;
    private final int maxFreshCount;

    public FreshLimitedQueueImpl(int maxFreshCount) {
        this.fresh = new ConcurrentLinkedQueue<>();
        this.old = new ConcurrentLinkedQueue<>();
        this.maxFreshCount = maxFreshCount;
    }

    @Override
    public List<T> getFresh() {
        return List.copyOf(fresh);
    }

    @Override
    public List<T> getOld() {
        return List.copyOf(old);
    }

    @Override
    public List<T> clearFresh() {
        List<T> result = List.copyOf(this.fresh);
        this.fresh.clear();
        return result;
    }

    @Override
    public List<T> clearOld() {
        List<T> result = List.copyOf(this.old);
        this.old.clear();
        return result;
    }

    @Override
    public boolean isFreshEmpty() {
        return this.fresh.isEmpty();
    }

    @Override
    public boolean isOldEmpty() {
        return this.old.isEmpty();
    }

    @Override
    public boolean contains(T obj) {
        return this.fresh.contains(obj) || this.old.contains(obj);
    }

    @Override
    public boolean freshContains(T obj) {
        return this.fresh.contains(obj);
    }

    @Override
    public boolean oldContains(T obj) {
        return this.old.contains(obj);
    }

    @Override
    public void clear() {
        this.clearFresh();
        this.clearOld();
    }

    @Override
    public List<T> removeAll() {
        List<T> removed = new ArrayList<>();
        removed.addAll(fresh);
        removed.addAll(old);

        this.clear();

        return removed;
    }

    @Override
    public void add(T obj) {
        if (fresh.size() == maxFreshCount) {
            old.add(fresh.remove());
        }
        fresh.add(obj);
    }

    @Override
    public void remove(T obj) {
        this.fresh.remove(obj);
        this.old.remove(obj);
    }
}
