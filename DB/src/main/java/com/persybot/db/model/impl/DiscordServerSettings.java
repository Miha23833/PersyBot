package com.persybot.db.model.impl;
//
//import com.persybot.db.model.HbTable;
//import org.hibernate.annotations.Cache;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.hibernate.annotations.Table;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//
//@Entity
//@Table(appliesTo = "DiscordServerSettings")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//public class DiscordServerSettings implements HbTable {
//    @Id
//    @Column(name = "serverId", updatable = false)
//    private Long serverId;
//
//    @Column(nullable = false)
//    private int volume = 100;
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String prefix = "..";
//
//    public DiscordServerSettings(Long serverId, int volume, String prefix) {
//        this.serverId = serverId;
//        this.volume = volume;
//        this.prefix = prefix;
//    }
//
//    public DiscordServerSettings(Long serverId) {
//        this.serverId = serverId;
//    }
//
//    public DiscordServerSettings() {
//
//    }
//
//    public int getVolume() {
//        return volume;
//    }
//
//    public void setVolume(int volume) {
//        this.volume = volume;
//    }
//
//    public String getPrefix() {
//        return prefix;
//    }
//
//    public void setPrefix(String prefix) {
//        this.prefix = prefix;
//    }
//
//    public Long getServerId() {
//        return serverId;
//    }
//}
