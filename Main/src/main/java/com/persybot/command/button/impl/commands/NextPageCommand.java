package com.persybot.command.button.impl.commands;

import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.template.impl.PagingMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.pojo.pagination.PageableMessages;
import net.dv8tion.jda.api.entities.Message;

public class NextPageCommand implements ButtonCommand {
    private final PageableMessages messages;

    public NextPageCommand() {
        messages = ServiceAggregatorImpl.getInstance().getService(StaticData.class).getPageableMessages();
    }

    @Override
    public void execute(ButtonCommandContext context) {
        long textChannelId = context.getEvent().getMessage().getTextChannel().getIdLong();
        Message currentMessage = context.getEvent().getMessage();
        PageableMessage pageableMessage = messages.get(textChannelId, context.getEvent().getMessageIdLong());

        if (!messages.contains(textChannelId, context.getEvent().getMessageIdLong()) || !pageableMessage.hasNext()) {
            return;
        }

        Message nextMessage = new PagingMessage(
                pageableMessage.next(),
                pageableMessage.hasPrev(),
                pageableMessage.hasNext()).template();

        currentMessage.editMessage(nextMessage).queue();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
