package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NowPlaying extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-now")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (audioPlayer.getPlayingTrack() == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            } else {
                URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                String videoID = uri.getQuery().split("=")[1];
                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                long duration = audioPlayer.getPlayingTrack().getInfo().length;
                Long hours = duration / 1000 / 60 / 60;
                Long minutes = duration / 1000 / 60 % 60;
                Long seconds = duration / 1000 % 60;
                Long l = 0L;
                if (musicManager.audioPlayer.getPlayingTrack() != null) {
                    l += musicManager.audioPlayer.getPlayingTrack().getPosition();
                }
                Long lhours = l / 1000 / 60 / 60;
                Long lminutes = l / 1000 / 60 % 60;
                Long lseconds = l / 1000 % 60;
                EmbedBuilder embed = new EmbedBuilder();
                String next;
                String now;
                if (musicManager.scheduler.queue.peek() == null) {
                    next = ":mute: None";
                } else {
                    next = "⏩ " + musicManager.scheduler.queue.peek().getInfo().title;
                }
                if (musicManager.audioPlayer.isPaused()) {
                    now = "⏸️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                } else {
                    now = "▶️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                }
                embed.setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                        .setAuthor("📀 Now Playing ", null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setDescription(audioPlayer.getPlayingTrack().getInfo().uri)
                        .setThumbnail(url)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                        .addField("Played", "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/" + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                        .addField("Now", now, true)
                        .addField("Next", next, true)
                        .setColor(15844367);
                event.getChannel().sendMessageEmbeds(embed.build()).complete();
            }
        }
    }

}
