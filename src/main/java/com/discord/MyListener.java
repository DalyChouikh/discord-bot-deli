package com.discord;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MyListener extends ListenerAdapter {


    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println("Logged in successfully as " + event.getJDA().getSelfUser().getName() + "#" + event.getJDA().getSelfUser().getDiscriminator() + "!");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event){
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.contains("!ping"))
        {
            MessageChannel channel = event.getChannel();
            MessageEmbed embed = new EmbedBuilder()
                                .setTitle("Message Received")
                                .setDescription(content)
                                .addField("Message sent :", "Pong!", false)
                                .setAuthor("Daly Chouikh")
                                .setFooter(String.format("Made with ❤️ by Daly#%s",event.getAuthor().getDiscriminator()), event.getAuthor().getAvatarUrl())
                                .setColor(event.getMember().getColorRaw())
                                .build();
            channel.sendMessageEmbeds(embed).queue();
            System.out.println(event.getAuthor().getAvatarUrl());
        }
    }
}