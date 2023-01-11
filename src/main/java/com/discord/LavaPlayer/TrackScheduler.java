package com.discord.LavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.net.URI;
import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack audioTrack) {
        if (!this.player.startTrack(audioTrack, true)) {
            this.queue.offer(audioTrack);
        }
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
        TextChannel textChannel = (TextChannel) track.getUserData();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(textChannel.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() != null) {
            URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
            String videoID = uri.getQuery().split("=")[1];
            String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
            long duration = audioPlayer.getPlayingTrack().getInfo().length;
            Long hours = duration / 1000 / 60 / 60;
            Long minutes = duration / 1000 / 60 % 60;
            Long seconds = duration / 1000 % 60;
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
            embed.setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                    .setAuthor("📀 Now Playing")
                    .setThumbnail(url)
                    .setFooter("Developed by Daly#3068 ❤️",
                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                    .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                    .addField("Now", now, true)
                    .addField("Next", next, true)
                    .setColor(15844367);

            textChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}


