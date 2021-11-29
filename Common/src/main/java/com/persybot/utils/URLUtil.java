package com.persybot.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

public interface URLUtil {
    static String getSiteDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    static boolean isYoutube(String url) {
        return Stream.of("youtube.com", "youtu.be").anyMatch(url::equalsIgnoreCase);
    }
}
