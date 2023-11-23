package com.discord.commands.music;


import com.discord.Bot;
import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Queue extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("queue")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (audioPlayer.getPlayingTrack() == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else {
                long duration = audioPlayer.getPlayingTrack().getDuration();
                Long hours = duration / 1000 / 60 / 60;
                Long minutes = duration / 1000 / 60 % 60;
                Long seconds = duration / 1000 % 60;
                long playTime = audioPlayer.getPlayingTrack().getPosition();
                Long playHours = playTime / 1000 / 60 / 60;
                Long playMinutes = playTime / 1000 / 60 % 60;
                Long playSeconds = playTime / 1000 % 60;
                int pos = 2;
                Pair<User, TextChannel> pair1 = (Pair<User, TextChannel>) audioPlayer.getPlayingTrack().getUserData();
                User user1 = pair1.getFirst();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("📀 Queue Requested by " + event.getMember().getUser().getName(),
                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .addField("Song #1",
                                audioPlayer.getPlayingTrack().getInfo().title + " **Played :** `"
                                        + String.format("%02d:%02d:%02d", playHours, playMinutes, playSeconds) + "/"
                                        + String.format("%02d:%02d:%02d", hours, minutes, seconds) + "`"
                                        + "** Requested by : ** `" + user1.getName()  + "`",
                                false)
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                for (AudioTrack track : musicManager.scheduler.queue) {
                    Long apprTime = track.getDuration();
                    Pair<User, TextChannel> pair = (Pair<User, TextChannel>) track.getUserData();
                    User user = pair.getFirst();
                    embed.addField("Song #" + pos,
                            track.getInfo().title + "** Length :** `" + String.format("%02d:%02d:%02d", apprTime / 1000 / 60 / 60, apprTime / 1000 / 60 % 60, apprTime / 1000 % 60) + "`"
                                    + "** Requested by : ** `" + user.getName()  + "`",
                            false);
                    pos++;
                }
                event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                return;
            }
        }

    }
}
