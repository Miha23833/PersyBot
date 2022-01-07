package com.persybot.staticdata.pojo;

import com.persybot.paginator.PageableMessage;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class PageableMessages {
    private final Map<Long, Map<Long, PageableMessage>> pagingMessages;

    public PageableMessages() {
        pagingMessages = new HashMap<>();
    }

    public void add(long textChannelId, long messageId, PageableMessage message) {
        this.pagingMessages.computeIfAbsent(textChannelId, k -> new HashMap<>()).put(messageId, message);
    }

    public boolean contains(long textChannelId, long messageId) {
        return containsMessage(textChannelId, messageId);
    }

    public boolean hasNext(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        return this.pagingMessages.get(textChannelId).get(messageId).hasNext();
    }

    public Message next(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        PageableMessage message = this.pagingMessages.get(textChannelId).get(messageId);

        if (message.hasNext()) {
            return message.next();
        }
        return null;
    }

    public Message current(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        return this.pagingMessages.get(textChannelId).get(messageId).getCurrent();
    }

    public boolean hasPrev(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        return this.pagingMessages.get(textChannelId).get(messageId).hasPrev();
    }

    public Message prev(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        PageableMessage message = this.pagingMessages.get(textChannelId).get(messageId);

        if (message.hasPrev()) {
            return message.prev();
        }
        return null;
    }

    private void checkForMessageExistence(long textChannelId, long messageId) {
        if (!containsMessage(textChannelId, messageId)) {
            throw new NullPointerException(notExistingExceptionMessage(textChannelId,messageId));
        }
    }

    private boolean containsMessage(long textChannelId, long messageId) {
        return this.pagingMessages.containsKey(textChannelId) && this.pagingMessages.get(textChannelId).containsKey(messageId);
    }

    private String notExistingExceptionMessage(long textChannelId, long messageId) {
        return String.format("Attempting to get access to not existing paging message: channel id: %s, message id: %s", textChannelId, messageId);
    }
}
