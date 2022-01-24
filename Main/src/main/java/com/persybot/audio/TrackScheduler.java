package com.persybot.audio;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;

import java.util.List;

public interface TrackScheduler {
    void addTrack(AudioTrackContext trackContext);

    void addPlaylist(AudioPlaylistContext playlistContext);

    boolean isEmpty();

    AudioTrackContext skipMultiple(int countOfSkips);

    AudioTrackContext nextTrack();

    void clear();

    void shuffle();

    List<String> queuedTracksTitles();
}
