package com.persybot.audio.audiomanager;

public class SongMetadata {
    private final String name;
    private final String artist;
    private final long duration;
    private final String url;

    public SongMetadata(String name, String artist, long duration, String url) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }
}
