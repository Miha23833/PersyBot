package com.persybot.staticdata.pojo.pagination;

import com.persybot.paginator.PageableMessage;

import java.util.HashMap;
import java.util.Map;

public class PageableMessages {
    private final Map<Long, Map<PAGE_TYPE, PageableMessage>> pagingMessages;

    public PageableMessages() {
        pagingMessages = new HashMap<>();
    }

    public void add(long textChannelId, PAGE_TYPE pageType, PageableMessage message) {
        this.pagingMessages.computeIfAbsent(textChannelId, k -> new HashMap<>()).put(pageType, message);
    }

    public PageableMessage get(long textChannelId, PAGE_TYPE type) {
        checkForMessageExistence(textChannelId, type);
        return this.pagingMessages.get(textChannelId).get(type);
    }

    private void checkForMessageExistence(long textChannelId, PAGE_TYPE type) {
        if (!contains(textChannelId, type)) {
            throw new NullPointerException(notExistingExceptionMessage(textChannelId,type));
        }
    }

    public boolean contains(long textChannelId, PAGE_TYPE type) {
        return this.pagingMessages.containsKey(textChannelId) && this.pagingMessages.get(textChannelId).containsKey(type);
    }

    private String notExistingExceptionMessage(long textChannelId, PAGE_TYPE type) {
        return String.format("Attempting to get access to not existing paging message: channel id: %s, message id: %s", textChannelId, type);
    }

    public enum PAGE_TYPE {
        PLAYER_QUEUE
    }
}
