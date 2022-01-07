package com.persybot.audio;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;

import java.util.List;

public interface TrackScheduler {
    void addTrack(AudioTrackContext trackContext);

    void addPlaylist(AudioPlaylistContext playlistContext);

    boolean isEmpty();

    void skipMultiple(int countOfSkips);

    void nextTrack();

    void clearQueue();

    void repeatTrack();

    void stopRepeating();

    void mixQueue();

    List<String> getQueuedTracks();
}
