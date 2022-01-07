package com.persybot.paginator;

import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.collections4.list.CursorableLinkedList;

public class PageableMessage implements Pageable<Message> {
    private final CursorableLinkedList<Message> pages;
    private final long messageId;

    private Message current;

    private CursorableLinkedList.Cursor<Message> cursor;
    
    public PageableMessage(long messageId) {
        this.messageId = messageId;
        this.pages = new CursorableLinkedList<>();
        
        this.cursor = this.pages.cursor();
    }

    @Override
    public void addPage(Message value) {
        this.pages.add(value);
    }

    @Override
    public boolean hasNext() {
        return this.cursor.hasNext();
    }

    @Override
    public Message next() {
        this.current = this.cursor.next();
        return this.current;
    }

    @Override
    public boolean hasPrev() {
        return this.cursor.hasPrevious();
    }

    @Override
    public Message prev() {
        current = this.cursor.previous();
        return this.current;
    }

    @Override
    public boolean isPointed() {
        return this.current != null;
    }

    @Override
    public Message getCurrent() {
        return this.current;
    }

    @Override
    public void pointToFirst() {
        if (this.pages.isEmpty()) {
            throw new NullPointerException("Attempting to move point in empty list");
        }
        this.cursor = this.pages.cursor(0);
        this.current = this.cursor.next();
    }

    public long getMessageId() {
        return messageId;
    }
}
