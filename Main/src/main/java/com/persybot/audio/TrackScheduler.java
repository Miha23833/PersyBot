package com.persybot.audio;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;

public interface TrackScheduler {
    void addTrack(AudioTrackContext trackContext);

    void addPlaylist(AudioPlaylistContext playlistContext);

    boolean isEmpty();

    void skipMultiple(int countOfSkips);

    void nextTrack();

    void clearQueue();

    List<AudioTrackInfo> getQueuedTracksInfo();

    void repeatTrack();

    void stopRepeating();

    void mixQueue();
}
