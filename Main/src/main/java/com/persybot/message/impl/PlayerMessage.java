package com.persybot.message.impl;

import com.persybot.message.BotMessage;
import com.persybot.message.PLAYER_BUTTON;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;

public class PlayerMessage implements BotMessage {
    private long messageId;

    private final ActionRow playerButtons;
    private final String currentTrack;

    public PlayerMessage(String currentTrack, boolean isTrackOnPause, boolean nextTrackAvailable) {
        Button playSwitch = isTrackOnPause ? PLAYER_BUTTON.RESUME.button(false) : PLAYER_BUTTON.PAUSE.button(false);
        Button skip = PLAYER_BUTTON.SKIP.button(nextTrackAvailable);
        Button stop = PLAYER_BUTTON.STOP.button(true);
        this.playerButtons = ActionRow.of(stop, playSwitch, skip);

        this.currentTrack = currentTrack;
    }

    @Override
    public Message getMessage() {
        MessageEmbed embedMessage = new EmbedBuilder().setColor(new Color(79, 117, 53))
                .setAuthor(currentTrack, null, "https://memepedia.ru/wp-content/uploads/2021/02/aboba-glad-valakas.jpg").build();


        return new MessageBuilder()
//                .append("**Current track:** ").append(currentTrack)
                .setEmbed(embedMessage)
                .setActionRows(playerButtons).build();
    }

    // TODO: remove method
    public static MessageEmbed embedMessage() {
        EmbedBuilder eb = new EmbedBuilder();

/*
    Set the title:
    1. Arg: title as string
    2. Arg: URL as string or could also be null
 */
        eb.setTitle("Title", null);

/*
    Set the color
 */
        eb.setColor(new Color(79, 117, 53));

/*
    Set the text of the Embed:
    Arg: text as string
 */
        eb.setDescription("Text");

/*
    Add fields to embed:
    1. Arg: title as string
    2. Arg: text as string
    3. Arg: inline mode true / false
 */
        eb.addField("Title of field", "test of field", false);

/*
    Add spacer like field
    Arg: inline mode true / false
 */
        eb.addBlankField(false);

/*
    Add embed author:
    1. Arg: name as string
    2. Arg: url as string (can be null)
    3. Arg: icon url as string (can be null)
 */
        eb.setAuthor("name", null, "https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");

/*
    Set footer:
    1. Arg: text as string
    2. icon url as string (can be null)
 */
        eb.setFooter("Text", "https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");

/*
    Set image:
    Arg: image url as string
 */
        eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");

/*
    Set thumbnail image:
    Arg: image url as string
 */
        eb.setThumbnail("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");
        return eb.build();
    }
}
