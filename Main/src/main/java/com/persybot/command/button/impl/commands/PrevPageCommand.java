package com.persybot.command.button.impl.commands;

import com.persybot.cache.service.CacheService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.message.template.impl.PagingMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.Message;

public class PrevPageCommand implements ButtonCommand {
    private final PageableMessageCache cache;

    public PrevPageCommand() {
        cache = ServiceAggregator.getInstance().get(CacheService.class).get(PageableMessageCache.class);
    }

    @Override
    public void execute(ButtonCommandContext context) {
        long textChannelId = context.getEvent().getMessage().getTextChannel().getIdLong();
        Message currentMessage = context.getEvent().getMessage();
        PageableMessage pageableMessage = cache.get(textChannelId, context.getEvent().getMessageIdLong());

        if (pageableMessage == null || !pageableMessage.hasPrev()) {
            return;
        }

        Message nextMessage = new PagingMessage(
                pageableMessage.prev(),
                pageableMessage.hasPrev(),
                pageableMessage.hasNext()).template();

        currentMessage.editMessage(nextMessage).queue();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
