package com.persybot.command.impl.commands;

import com.google.common.collect.Lists;
import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.TextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.PagingMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import com.persybot.staticdata.pojo.pagination.PageableMessages;
import net.dv8tion.jda.api.MessageBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ShowQueueCommand implements TextCommand {
    private final PageableMessages messages;
    private final ChannelService channelService;
    public ShowQueueCommand() {
        messages = ServiceAggregatorImpl.getInstance().getService(StaticData.class).getPageableMessages();
        channelService = ServiceAggregatorImpl.getInstance().getService(ChannelService.class);
    }

    @Override
    public void execute(TextCommandContext context) {
        Optional<Channel> channel = Optional.ofNullable(channelService.getChannel(context.getGuildId()));
        List<String> queue = new LinkedList<>();
        if (channel.isPresent()) {
            queue = channel.get().getAudioPlayer().getQueuedTracks();
        }
        if (queue.isEmpty()) {
            return;
        }
        PageableMessage pageableMessage = new PageableMessage(context.getEvent().getMessageIdLong());

        List<List<String>> pageContents = Lists.partition(queue, 8);

        for (List<String> pageContent: pageContents) {
            StringBuilder builder = new StringBuilder();
            for (String row : pageContent) {
                builder.append(row).append("\n");
            }
            pageableMessage.addPage(new MessageBuilder(builder.toString()).build());
        }
        pageableMessage.pointToFirst();
        context.getEvent().getChannel().sendMessage(new PagingMessage(pageableMessage.getCurrent(), false, pageContents.size() > 1).template())
                .queue(success -> {
                    messages.add(success.getTextChannel().getIdLong(), success.getIdLong(), pageableMessage);
                    new MessageSendSuccess<>(MessageType.PLAYER_QUEUE, success).accept(success);
                });
    }

    @Override
    public String describe(TextCommandContext context) {
        return TEXT_COMMAND.QUEUE.describeText();
    }
}
