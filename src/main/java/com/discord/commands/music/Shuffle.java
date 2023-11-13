package com.discord.commands.music;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shuffle extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("shuffle")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else {
                final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
                final AudioPlayer audioPlayer = musicManager.audioPlayer;
                if (musicManager.scheduler.queue.isEmpty()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("🔊 Queue is empty")
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else {
                    List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);;
                    Collections.shuffle(trackList);
                    musicManager.scheduler.queue.clear();
                    musicManager.scheduler.queue.addAll(trackList);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("\uD83E\uDDF9 Queue shuffled by " + event.getUser().getName() + "#" + event.getUser().getDiscriminator(),
                                    null, event.getUser().getEffectiveAvatarUrl())
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                    event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                    return;
                }
            }
        }
    }
}
