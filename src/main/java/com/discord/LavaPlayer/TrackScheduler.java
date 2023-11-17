package com.discord.LavaPlayer;

import com.discord.events.AutoLeave;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

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
        Pair<User, TextChannel> pair = (Pair<User, TextChannel>) track.getUserData();
        TextChannel textChannel = pair.getSecond();
        User user = pair.getFirst();
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
                    .setAuthor("📀 Now Playing ")
                    .setDescription("** Requested by : ** `" + user.getName() +  "`")
                    .setThumbnail(url)
                    .setFooter("Developed by Daly. ❤️",
                            "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png")
                    .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                    .addField("Now", now, true)
                    .addField("Next", next, true)
                    .setColor(15844367);

            textChannel.sendMessageEmbeds(embed.build()).complete();
        }
        AudioManager audioManager = textChannel.getGuild().getAudioManager();
        AutoLeave autoLeave = new AutoLeave();
        autoLeave.scheduleLeave(audioManager, musicManager);
    }


}


