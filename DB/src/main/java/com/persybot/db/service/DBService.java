package com.persybot.db.service;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.PlayList;
import com.persybot.service.Service;

import java.util.Map;

public interface DBService extends Service {
    Long saveDiscordServerSettings(DiscordServerSettings entity);
    DiscordServerSettings getDiscordServerSettings(long id);
    boolean updateDiscordServerSettings(DiscordServerSettings entity);

    Long saveDiscordServer(DiscordServer entity);
    DiscordServer getDiscordServer(long id);
    boolean updateDiscordServer(DiscordServer entity);

    PlayList getPlaylistById(long id);
    PlayList getPlaylistByName(String name, long serverId);
    Map<Long, PlayList> getAllPlaylistForServer(Long serverId);
    boolean updatePlayList(PlayList entity);
    Long saveOrUpdatePlayList(PlayList playList);
    boolean isPlaylistExists(PlayList playList);
    Long savePlayList(PlayList playList);
}
