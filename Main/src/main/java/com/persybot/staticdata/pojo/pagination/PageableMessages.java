package com.persybot.staticdata.pojo.pagination;

import com.persybot.paginator.PageableMessage;

import java.util.HashMap;
import java.util.Map;

public class PageableMessages {
    private final Map<Long, Map<PageableMessageContext, PageableMessage>> pagingMessages;

    public PageableMessages() {
        pagingMessages = new HashMap<>();
    }

    public void add(long textChannelId, PAGE_TYPE pageType, long msgId, PageableMessage message) {
        this.pagingMessages.computeIfAbsent(textChannelId, k -> new HashMap<>()).put(new PageableMessageContext(pageType, msgId), message);
    }

    public PageableMessage get(long textChannelId, long msgId) {
        checkForMessageExistence(textChannelId, msgId);
        PageableMessageContext key = this.pagingMessages.get(textChannelId).keySet().stream().filter(x -> x.msgId == msgId).findFirst().orElse(null);

        if (key == null) {
            return null;
        }
        return this.pagingMessages.get(textChannelId).get(key);
    }

    private void checkForMessageExistence(long textChannelId, long msgId) {
        if (!contains(textChannelId, msgId)) {
            throw new NullPointerException(notExistingExceptionMessage(textChannelId, msgId));
        }
    }

    public boolean contains(long textChannelId, long msgId) {
        PageableMessageContext key = this.pagingMessages.get(textChannelId).keySet().stream().filter(x -> x.msgId == msgId).findFirst().orElse(null);
        return key != null;
    }

    private String notExistingExceptionMessage(long textChannelId, long msgId) {
        return String.format("Attempting to get access to not existing paging message: channel id: %s, message id: %s", textChannelId, msgId);
    }

    private static class PageableMessageContext {
        private final PAGE_TYPE pageType;
        private final long msgId;

        private PageableMessageContext(PAGE_TYPE pageType, long msgId) {
            this.pageType = pageType;
            this.msgId = msgId;
        }

        @Override
        public int hashCode() {
            return pageType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (!(obj instanceof PageableMessageContext)) {
                return false;
            }

            return this.pageType.equals(((PageableMessageContext) obj).pageType);
        }
    }

    public enum PAGE_TYPE {
        PLAYER_QUEUE,
        PLAYLISTS
    }
}
