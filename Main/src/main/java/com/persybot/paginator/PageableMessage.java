package com.persybot.paginator;

import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.collections4.list.CursorableLinkedList;

public class PageableMessage {
    private final CursorableLinkedList<Message> pages;
    private final CursorableLinkedList.Cursor<Message> cursor;

    private final Long messageId;

    private Message current;

    private PageableMessage(Long messageId, CursorableLinkedList<Message> data) {
        this.messageId = messageId;
        this.pages = data;

        this.cursor = this.pages.cursor(0);
        this.current = this.cursor.next();
    }

    public boolean hasNext() {
        return this.cursor.hasNext();
    }

    public Message next() {
        this.current = this.cursor.next();
        return this.current;
    }

    public boolean hasPrev() {
        return this.cursor.hasPrevious();
    }

    public Message prev() {
        current = this.cursor.previous();
        return this.current;
    }

    public Message getCurrent() {
        return this.current;
    }

    public Long getMessageId() {
        return messageId;
    }

    private CursorableLinkedList<Message> getPages() {
        return this.pages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CursorableLinkedList<Message> messages;

        private Builder() {
            this.messages = new CursorableLinkedList<>();
        }

        public Builder addMessage(Message message) {
            this.messages.add(message);
            return this;
        }

        public Message get(int index) {
            if (index >= messages.size()) {
                throw new IndexOutOfBoundsException(String.format("Size is %s, but index was: %s", messages.size(), index));
            }
            return messages.get(index);
        }

        public int size() {
            return this.messages.size();
        }

        public PageableMessage build(Long messageId) {
            return new PageableMessage(messageId, messages);
        }
    }

}
