package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;

public abstract class AbstractBotAction {
    protected final Channel actingChannel;

    public AbstractBotAction(Channel actingChannel) {this.actingChannel = actingChannel;}


}
