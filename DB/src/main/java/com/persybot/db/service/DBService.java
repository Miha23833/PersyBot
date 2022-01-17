package com.persybot.db.service;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.entity.PlayList;
import com.persybot.db.entity.ServerAudioSettings;
import com.persybot.service.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DBService extends Service {
    Optional<Long> saveDiscordServerSettings(DiscordServerSettings entity);
    Optional<DiscordServerSettings> getDiscordServerSettings(long id);
    boolean updateDiscordServerSettings(DiscordServerSettings entity);

    Optional<Long> saveDiscordServer(DiscordServer entity);
    Optional<DiscordServer> getDiscordServer(long id);
    boolean updateDiscordServer(DiscordServer entity);

    Optional<PlayList> getPlaylistById(long id);
    Optional<PlayList> getPlaylistByName(String name, long serverId);
    Optional<Map<Long, PlayList>> getAllPlaylistForServer(Long serverId);
    boolean updatePlayList(PlayList entity);
    Optional<Long> saveOrUpdatePlayList(PlayList playList);
    boolean isPlaylistExists(PlayList playList);
    Optional<Long> savePlayList(PlayList playList);

    Optional<Long> saveServerAudioSettings(ServerAudioSettings entity);
    Optional<ServerAudioSettings> getServerAudioSettings(long id);
    boolean updateServerAudioSettings(ServerAudioSettings entity);

    Optional<EqualizerPreset> getEqPresetByName(String name);
    Optional<List<EqualizerPreset>> getAllEqPresets();
}
