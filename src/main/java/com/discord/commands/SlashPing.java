package com.discord.commands;

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
            embed.setAuthor("Ping", null, "https://cdn.discordapp.com/avatars/917074546933260399/0a8290bb752d9f70e2b1af21e017b853.webp?size=80")
                 .addField(":signal_strength: Message Latency", Long.toString(event.getJDA().getGatewayPing()), true)
                 .setColor(15844367)
                 .setFooter("Developed by Daly. ❤️",
                            "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");

            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}