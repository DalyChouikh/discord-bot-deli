package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Seek extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-seek")) {
            if (message.length != 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the song number in the queue")
                        .setTitle("👉  You can use -seek [Song position in queue]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setTitle("👉 You can use -play [song name/URL]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            try {
                int queue = Integer.parseInt(message[1]) - 1;
                if (queue == 0 && audioPlayer.getPlayingTrack() != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("⛔ To seek the current song infos")
                            .setTitle("👉 You can use -now")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                } else if (queue == 0 && audioPlayer.getPlayingTrack() == null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ There is currently no track playing")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                } else if (queue > musicManager.scheduler.queue.size()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ No song found with that position")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
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
                            embed.setAuthor(
                                    "🎵 " + track.getInfo().title + " (Requested by "
                                            + event.getMember().getUser().getName()
                                            + "#"
                                            + event.getMember().getUser().getDiscriminator() + ")",
                                    null, event.getMember().getUser().getEffectiveAvatarUrl())
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
                                    .addField("Postion in Queue ", Integer.toString(queue + 1), true)
                                    .setColor(15844367)
                                    .setFooter("Developed by Daly#3068 ❤️",
                                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();
                            return;
                        }
                        i++;
                    }
                }
            } catch (NumberFormatException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ Please enter a valid number")
                        .setTitle("👉 Use --seek [Song position in queue]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

        }
    }
}
