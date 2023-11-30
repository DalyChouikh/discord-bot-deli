package com.discord.LavaPlayer;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discord.Bot;
import com.github.topisenpai.lavasrc.spotify.SpotifyAudioTrack;
import com.github.topisenpai.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioPlayerManager.registerSourceManager(new SpotifySourceManager(null,System.getenv("SPOTIFY_CLIENT_ID"),System.getenv("SPOTIFY_CLIENT_SECRET"),
                System.getenv("COUNTRY_CODE"), this.audioPlayerManager));
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl, User user) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                audioTrack.setUserData(new Pair<User,TextChannel>(user,(TextChannel) textChannel));
                URI uri = URI.create(audioTrack.getInfo().uri);
                String videoID = uri.getQuery().split("=")[1];
                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                long duration = audioTrack.getInfo().length;
                System.out.println(Duration.ofMillis(duration));
                long playTime = 0L;
                int queue = musicManager.scheduler.queue.size() + 1;
                if (!musicManager.scheduler.queue.isEmpty()) {
                    for (AudioTrack track : musicManager.scheduler.queue) {
                        playTime += track.getDuration();
                    }
                    playTime += (musicManager.scheduler.player.getPlayingTrack().getDuration() - musicManager.scheduler.player.getPlayingTrack().getPosition()) - audioTrack.getDuration();
                }
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
                embed.setTitle("🎵 " + audioTrack.getInfo().title,audioTrack.getInfo().uri)
                        .setAuthor("📀 Adding to queue ")
                        .setDescription("** Requested by : ** `" + user.getName()+ "`")
                        .setThumbnail(url)
                        .setFooter("Developed by Daly. ❤️", Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl())
                        .addField("Length", "🕐 " + DurationFormatUtils.formatDuration(duration, "HH:mm:ss"), true)
                        .addField("Now", now, true)
                        .addField("Next", next, true)
                        .addField("Approx. time to play", "🕐 " + DurationFormatUtils.formatDuration(playTime, "HH:mm:ss"), true)
                        .addField("Position in Queue", queue + "/" + (musicManager.scheduler.queue.size() + 1), true)
                        .setColor(15844367);
                textChannel.sendMessageEmbeds(embed.build()).queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                long length = 0L;
                if (trackUrl.contains("&list") || (trackUrl.contains("open.spotify.com") && !trackUrl.contains("track"))) {
                    System.out.println("playlist");
                    for (AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                        length += track.getDuration();
                        track.setUserData(new Pair<User,TextChannel>(user,textChannel));
                    }
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("✅ Playlist (" + tracks.size() + " songs) added", trackUrl.contains("spotify") ? trackUrl.replaceAll("ytsearch:", "") : trackUrl)
                            .setDescription("** Requested by : ** `" + user.getName() + "`")
                            .addField("Length", "🕐 " + DurationFormatUtils.formatDuration(length, "HH:mm:ss"), true)
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️", Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } else if (!tracks.isEmpty()) {
                    System.out.println("track");
                    AudioTrack playingTrack = tracks.get(0);
                    musicManager.scheduler.queue(playingTrack);
                    playingTrack.setUserData(new Pair<User,TextChannel>(user,(TextChannel) textChannel));
                    URI uri = URI.create(playingTrack.getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    long duration = playingTrack.getInfo().length;
                    long playTime = 0L;
                    int queue = musicManager.scheduler.queue.size() + 1;
                    if (!musicManager.scheduler.queue.isEmpty()) {
                        for (AudioTrack track : musicManager.scheduler.queue) {
                            playTime += track.getDuration();
                        }
                        playTime += (musicManager.scheduler.player.getPlayingTrack().getDuration() - musicManager.scheduler.player.getPlayingTrack().getPosition()) - playingTrack.getDuration();
                    }
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
                    embed.setTitle("🎵 " + playingTrack.getInfo().title, playingTrack.getInfo().uri)
                            .setAuthor("📀 Adding to queue")
                            .setDescription("** Requested by : ** `" + user.getName() + "`")
                            .setThumbnail(url)
                            .setFooter("Developed by Daly. ❤️", Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl())
                            .addField("Length", "🕐 " + DurationFormatUtils.formatDuration(duration, "HH:mm:ss"), true)
                            .addField("Now", now, true)
                            .addField("Next", next, true)
                            .addField("Approx. time to play", "🕐 " + DurationFormatUtils.formatDuration(playTime, "HH:mm:ss"), true)
                            .addField("Position in Queue", queue + "/" + (musicManager.scheduler.queue.size() + 1), true)
                            .setColor(15844367);
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                String search = trackUrl.replaceAll("ytsearch:", "").replaceAll("audio", "");
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("⛔ No matches were found for :", search, true).setColor(15844367)
                        .setFooter("Developed by Daly. ❤️", Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                textChannel.sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println(e.getMessage());
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("❌ Something happened, Couldn't load track").setColor(15844367).setFooter("Developed by Daly. ❤️", Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());

                textChannel.sendMessageEmbeds(embed.build()).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

}
