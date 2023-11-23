package com.discord.commands;

import com.discord.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class SlashPing extends ListenerAdapter {


    public void onReady(ReadyEvent event) {
        System.out.println("Logged in successfully as " + event.getJDA().getSelfUser().getName()  + "!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        
        if(event.getName().equalsIgnoreCase("ping")){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Ping", null, event.getJDA().getSelfUser().getAvatarUrl())
                 .addField(":signal_strength: Message Latency", Long.toString(event.getJDA().getGatewayPing()), true)
                 .setColor(15844367)
                 .setFooter("Developed by Daly. ❤️",
                         Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());

            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}