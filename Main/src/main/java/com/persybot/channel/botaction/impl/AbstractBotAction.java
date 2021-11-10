package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;

public abstract class AbstractBotAction {
    protected final StaticData staticData;
    protected final Channel actingChannel;

    public AbstractBotAction(Channel actingChannel) {
        this.staticData = ServiceAggregatorImpl.getInstance().getService(StaticData.class);
        this.actingChannel = actingChannel;
    }


}
