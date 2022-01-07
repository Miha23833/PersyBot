package com.persybot.staticdata.pojo.pagination;

import com.persybot.message.service.MessageControlService;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;

import java.util.HashMap;
import java.util.Map;

public class PageableMessages {
    private static MessageControlService messageControlService;
    private final Map<Long, Map<Long, PageableMessage>> pagingMessages;

    public PageableMessages() {
        if (messageControlService == null) {
            messageControlService = ServiceAggregatorImpl.getInstance().getService(MessageControlService.class);
        }
        pagingMessages = new HashMap<>();
    }

    public void add(long textChannelId, long messageId, PageableMessage message) {
        this.pagingMessages.computeIfAbsent(textChannelId, k -> new HashMap<>());
        if (this.pagingMessages.get(textChannelId).containsKey(messageId)) {
            messageControlService.deleteMessage(textChannelId, messageId);
        }
        this.pagingMessages.get(textChannelId).put(messageId, message);
    }

    public PageableMessage get(long textChannelId, long messageId) {
        checkForMessageExistence(textChannelId, messageId);
        return this.pagingMessages.get(textChannelId).get(messageId);
    }

    private void checkForMessageExistence(long textChannelId, long messageId) {
        if (!contains(textChannelId, messageId)) {
            throw new NullPointerException(notExistingExceptionMessage(textChannelId,messageId));
        }
    }

    public boolean contains(long textChannelId, long messageId) {
        return this.pagingMessages.containsKey(textChannelId) && this.pagingMessages.get(textChannelId).containsKey(messageId);
    }

    private String notExistingExceptionMessage(long textChannelId, long messageId) {
        return String.format("Attempting to get access to not existing paging message: channel id: %s, message id: %s", textChannelId, messageId);
    }
}
