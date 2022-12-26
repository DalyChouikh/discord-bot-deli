package com.discord.commands.music;

import java.util.Iterator;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Queue extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-queue")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
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
            if (audioPlayer.getPlayingTrack() == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else {
                long duration = audioPlayer.getPlayingTrack().getDuration();
                Long hours = duration / 1000 / 60 / 60;
                Long minutes = duration / 1000 / 60 % 60;
                Long seconds = duration / 1000 % 60;
                long playTime = audioPlayer.getPlayingTrack().getPosition();
                Long playHours = playTime / 1000 / 60 / 60;
                Long playMinutes = playTime / 1000 / 60 % 60;
                Long playSeconds = playTime / 1000 % 60;
                Iterator<AudioTrack> iterator = musicManager.scheduler.queue.iterator();
                int pos = 2;
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("📀 Queue (Requested by " + event.getMember().getUser().getName() + "#"
                        + event.getMember().getUser().getDiscriminator() + ")",
                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .addField("Song #1",
                                audioPlayer.getPlayingTrack().getInfo().title + " **Played :** `"
                                        + String.format("%02d:%02d:%02d", playHours, playMinutes, playSeconds) + "/"
                                        + String.format("%02d:%02d:%02d", hours, minutes, seconds) + "`",
                                false)
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                for (AudioTrack track : musicManager.scheduler.queue) {
                    Long apprTime = track.getDuration();
                    embed.addField("Song #" + pos,
                            track.getInfo().title + "** Length :** `" + String.format("%02d:%02d:%02d", apprTime / 1000 / 60 / 60, apprTime / 1000 / 60 % 60, apprTime / 1000 % 60) + "`",
                            false);
                    pos++;
                }
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        }

    }
}
