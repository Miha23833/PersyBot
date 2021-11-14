package com.persybot.collections.list;

import java.util.List;

public interface FreshLimitedQueue<T> {
    List<T> getFresh();
    List<T> getOld();

    List<T> clearFresh();
    List<T> clearOld();

    boolean isFreshEmpty();
    boolean isOldEmpty();

    boolean contains(T obj);
    boolean freshContains(T obj);
    boolean oldContains(T obj);

    void clear();
    List<T> removeAll();

    void add(T obj);

    void remove(T obj);
}
