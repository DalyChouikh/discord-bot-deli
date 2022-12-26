package com.discord.commands.trivia;

import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Trivia extends ListenerAdapter{
    public static int number = 10;
    public static Message msg;
    public static TextChannel channel;
    public void onMessageReceived(@Nonnull MessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }
        String[] content = event.getMessage().getContentRaw().split(" ");
        if(content[0].equalsIgnoreCase("--trivia") && content[1].equalsIgnoreCase("start")){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Starting Trivia")
                 .setThumbnail("https://cdn-icons-png.flaticon.com/512/5692/5692030.png")
                 .addField("Choose the number of Questions :", ":one: 5 Questions  :two: 10 Questions  :three: 15 Questions\n  :four: 20 Questions", true)
                 .setColor(15844367);      
            Message message = event.getMessage().getChannel().sendMessageEmbeds(embed.build()).complete();
            msg = message;
            channel = (TextChannel) msg.getChannel();
            Emoji one = Emoji.fromUnicode("1️⃣");
            Emoji two = Emoji.fromUnicode("2️⃣");
            Emoji three = Emoji.fromUnicode("3️⃣");
            Emoji four = Emoji.fromUnicode("4️⃣");
            message.addReaction(one).complete();
            message.addReaction(two).complete();
            message.addReaction(three).complete();
            message.addReaction(four).complete();
            event.getJDA().addEventListener(new SingleReactionListenerForNumber());
        }

    }

    

}
