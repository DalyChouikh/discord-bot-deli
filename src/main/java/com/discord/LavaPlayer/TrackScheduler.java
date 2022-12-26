package com.discord.LavaPlayer;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
            final GuildMusicManager musicManager = PlayerManager.getInstance()
                    .getMusicManager(PlayerManager.getInstance().getTextChannel().getGuild());
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
                if(musicManager.audioPlayer.isPaused()){
                    now = "⏸️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                }
                else{
                    now = "▶️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                } 
                embed.setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                        .setAuthor("📀 Now Playing")
                        .setDescription(audioPlayer.getPlayingTrack().getInfo().uri)
                        .setThumbnail(url)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                        .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                        .addField("Now", now, true)
                        .addField("Next", next, true)
                        .setColor(15844367);
                PlayerManager.getInstance().getTextChannel().sendMessageEmbeds(embed.build()).complete();
            } 
        }
        // if (!queue.isEmpty()) {
        // Timer timer = new Timer();
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // System.out.println("ping");
        // if
        // (PlayerManager.getInstance().getTextChannel().getGuild().getAudioManager().isConnected()
        // &&
        // !PlayerManager.getInstance().getTextChannel().getGuild().getAudioManager().getSendingHandler().canProvide())
        // {
        // System.out.println("pong");
        // PlayerManager.getInstance().getTextChannel().getGuild().getAudioManager().closeAudioConnection();
        // }
        // }
        // }, 5000);
        // }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
    }
}
