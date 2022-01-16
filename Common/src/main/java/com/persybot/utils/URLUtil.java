package com.persybot.utils;

import com.persybot.logger.impl.PersyBotLogger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Stream;

public interface URLUtil {

    // todo use regex to determine if url is playable
    Set<String> AVAILABLE_PLAYING_HOSTS = Set.of(
            "youtube.com", "youtu.be",
            "soundcloud.com",
            "open.spotify.com",
            "bandcamp.com",
            "vimeo.com",
            "go.twitch.com", "twitch.tv",
            "beam.pro",
            "getyarn.io"
    );

    static boolean isPlayableLink(String url) {
        if (!isUrl(url)) {
            return false;
        }
        String domain;
        try {
            domain = getSiteDomain(url);
        } catch (URISyntaxException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
        return AVAILABLE_PLAYING_HOSTS.contains(domain);
    }

    static boolean isUrl(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static String getSiteDomain(String url) throws URISyntaxException {
        String domain = new URI(url).getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    static boolean isDomainYoutube(String domain) {
        return Stream.of("youtube.com", "youtu.be").anyMatch(domain::equalsIgnoreCase);
    }
}
