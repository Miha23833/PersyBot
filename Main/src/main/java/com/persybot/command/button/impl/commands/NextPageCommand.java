package com.persybot.command.button.impl.commands;

import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.template.impl.PagingMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.pojo.PageableMessages;
import net.dv8tion.jda.api.entities.Message;

public class NextPageCommand implements ButtonCommand {
    private final PageableMessages messages;

    public NextPageCommand() {
        messages = ServiceAggregatorImpl.getInstance().getService(StaticData.class).getPageableMessages();
    }

    @Override
    public void execute(ButtonCommandContext context) {
        long textChannelId = context.getEvent().getMessage().getTextChannel().getIdLong();
        long messageId = context.getEvent().getMessageIdLong();
        Message currentMessage = context.getEvent().getMessage();

        if (!messages.contains(textChannelId, messageId) || !messages.hasNext(textChannelId, messageId)) {
            return;
        }

        Message nextMessage = new PagingMessage(
                messages.next(textChannelId, messageId),
                messages.hasPrev(textChannelId, messageId),
                messages.hasNext(textChannelId, messageId)).template();

        currentMessage.editMessage(nextMessage).queue();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
