package com.persybot.message.template;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface MessageTemplate {
    MessageCreateData template();
}
