package com.discord.commands.music;

import java.net.URI;

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

public class Seek extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("seek")) {
            if (event.getOptionsByName("position").isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the song number in the queue")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            try {
                int queue = event.getOption("position").getAsInt() - 1;
                if (queue == 0 && audioPlayer.getPlayingTrack() != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("⛔ To seek the current song infos")
                            .setTitle("👉 Use /now")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else if (queue == 0 && audioPlayer.getPlayingTrack() == null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ There is currently no track playing")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else if (queue > musicManager.scheduler.queue.size()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ No song found with that position")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else {
                    String next;
                    String now;
                    if (musicManager.audioPlayer.isPaused()) {
                        now = "⏸️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                    } else {
                        now = "▶️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                    }
                    if (musicManager.scheduler.queue.peek() == null) {
                        next = ":mute: None";
                    } else {
                        next = "⏩ " + musicManager.scheduler.queue.peek().getInfo().title;
                    }
                    int i = 1;
                    Long apprTime = audioPlayer.getPlayingTrack().getDuration()
                            - audioPlayer.getPlayingTrack().getPosition();
                    for (AudioTrack track : musicManager.scheduler.queue) {
                        AudioTrack needTrack = track;
                        apprTime += track.getDuration();
                        if (i == queue) {
                            apprTime -= needTrack.getDuration();
                            URI uri = URI.create(track.getInfo().uri);
                            String videoID = uri.getQuery().split("=")[1];
                            String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                            Long duration = needTrack.getDuration();
                            EmbedBuilder embed = new EmbedBuilder();
                            Pair<User, TextChannel> pair = (Pair<User, TextChannel>) track.getUserData();
                            User user = pair.getFirst();
                            embed.setTitle(
                                    "🎵 " + track.getInfo().title , track.getInfo().uri)
                                    .setDescription("** Requested by : ** `" + user.getName() + "#" + user.getDiscriminator() + "`")
                                    .setThumbnail(url)
                                    .addField("Now ", now, true)
                                    .addField("Next", next, true)
                                    .addField("Length",
                                            "🕐 " + String.format("%02d:%02d:%02d", duration / 1000 / 60 / 60,
                                                    duration / 1000 / 60 % 60, duration / 1000 % 60),
                                            true)
                                    .addField("Approx. time to play ",
                                            "🕐 " + String.format("%02d:%02d:%02d", apprTime / 1000 / 60 / 60,
                                                    apprTime / 1000 / 60 % 60, apprTime / 1000 % 60),
                                            true)
                                    .addField("Position in Queue ", Integer.toString(queue + 1), true)
                                    .setColor(15844367)
                                    .setFooter("Developed by Daly#3068 ❤️",
                                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                            event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                            return;
                        }
                        i++;
                    }
                }
            } catch (NumberFormatException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ Please enter a valid number")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }

        }
    }
}
