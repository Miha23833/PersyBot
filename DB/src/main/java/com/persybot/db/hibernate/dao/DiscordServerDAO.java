package com.persybot.db.hibernate.dao;

import com.persybot.db.entity.DiscordServer;
import org.hibernate.SessionFactory;

public class DiscordServerDAO extends DAO<DiscordServer> {
    public DiscordServerDAO(SessionFactory sessionFactory) {
        super(sessionFactory, DiscordServer.class);
    }
}
