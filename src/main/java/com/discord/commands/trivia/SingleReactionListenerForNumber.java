package com.discord.commands.trivia;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SingleReactionListenerForNumber extends ListenerAdapter {
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot()){
            return;
        }
        if(event.getMessageIdLong() == Trivia.msg.getIdLong() && event.getEmoji().asUnicode().equals(Emoji.fromUnicode("1️⃣"))){
            Trivia.number = 5;
            System.out.println(Trivia.number);
            event.getJDA().removeEventListener(this);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Starting Trivia")
                 .setThumbnail("https://cdn-icons-png.flaticon.com/512/5692/5692030.png")
                 .addField("Choose the difficulty of the Questions : ", ":one: Any Type  :two: Easy :three: Medium :four: Hard", true)
                 .setColor(15844367);      
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if(event.getMessageIdLong() == Trivia.msg.getIdLong() && event.getEmoji().asUnicode().equals(Emoji.fromUnicode("2️⃣"))){
            Trivia.number = 10;
            System.out.println(Trivia.number);
            event.getJDA().removeEventListener(this);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Starting Trivia")
                 .setThumbnail("https://cdn-icons-png.flaticon.com/512/5692/5692030.png")
                 .addField("Choose the difficulty of the Questions : ", ":one: Any Type  :two: Easy :three: Medium :four: Hard", true)
                 .setColor(15844367);      
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            event.getJDA().addEventListener(new Difficulty());
            return;
        }
        if(event.getMessageIdLong() == Trivia.msg.getIdLong() && event.getEmoji().asUnicode().equals(Emoji.fromUnicode("3️⃣"))){
            Trivia.number = 15;
            System.out.println(Trivia.number);
            event.getJDA().removeEventListener(this);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Starting Trivia")
                 .setThumbnail("https://cdn-icons-png.flaticon.com/512/5692/5692030.png")
                 .addField("Choose the difficulty of the Questions : ", ":one: Any Type  :two: Easy :three: Medium :four: Hard", true)
                 .setColor(15844367);      
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if(event.getMessageIdLong() == Trivia.msg.getIdLong() && event.getEmoji().asUnicode().equals(Emoji.fromUnicode("4️⃣"))){
            Trivia.number = 20;
            System.out.println(Trivia.number);
            event.getJDA().removeEventListener(this);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Starting Trivia")
                 .setThumbnail("https://cdn-icons-png.flaticon.com/512/5692/5692030.png")
                 .addField("Choose the difficulty of the Questions : ", ":one: Any Type  :two: Easy :three: Medium :four: Hard", true)
                 .setColor(15844367);      
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
    }
}
