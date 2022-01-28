package com.persybot.command.impl.commands;

import com.google.common.collect.Lists;
import com.persybot.cache.service.CacheService;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.TextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.message.PAGEABLE_MESSAGE_TYPE;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ShowQueueCommand implements TextCommand {
    private final ChannelService channelService;
    private final PageableMessageCache cache;

    public ShowQueueCommand() {
        ServiceAggregator serviceAggregator = ServiceAggregator.getInstance();
        channelService = serviceAggregator.get(ChannelService.class);
        cache = serviceAggregator.get(CacheService.class).get(PageableMessageCache.class);
    }

    @Override
    public void execute(TextCommandContext context) {
        Optional<Channel> channel = Optional.ofNullable(channelService.getChannel(context.getGuildId()));
        List<String> queue = new LinkedList<>();
        if (channel.isPresent()) {
            if (!channel.get().hasInitiatedAudioPlayer()) {
                return;
            }
            queue = channel.get().getAudioPlayer().getQueuedTracks();
        }
        if (queue.isEmpty()) {
            return;
        }

        PageableMessage.Builder rsp = PageableMessage.builder();
        Lists.partition(queue, 8)
                .stream()
                .map(part -> new InfoMessage("Now playing tracks:", String.join("\n ", part)).template())
                .forEach(rsp::addMessage);

        BotUtils.sendPageableMessage(rsp, context.getEvent().getChannel(), PAGEABLE_MESSAGE_TYPE.PLAYER_QUEUE, cache);

    }

    @Override
    public String describe(TextCommandContext context) {
        return "Shows playing queue";
    }
}
