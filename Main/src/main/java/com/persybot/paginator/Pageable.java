package com.persybot.paginator;

public interface Pageable<T> {
    void addPage(T value);

    boolean hasNext();
    T next();
    boolean hasPrev();
    T prev();
    boolean isPointed();
    T getCurrent();

    void pointToFirst();
}
