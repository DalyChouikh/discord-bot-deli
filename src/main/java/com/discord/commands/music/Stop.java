package com.discord.commands.music;


import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Stop extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("stop")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).complete();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).complete();
                return;
            } else {
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.stopTrack();
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.setPaused(false);
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.clear();
                event.getGuild().getAudioManager().closeAudioConnection();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                        "⏹️ Stopped by " + event.getMember().getUser().getName(),
                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setTitle("🧹 Queue cleared")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(false).complete();
            }
        }
    }
}
