package com.discord.LavaPlayer;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
   
    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                audioTrack.setUserData((TextChannel) textChannel);
                URI uri = URI.create(audioTrack.getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    Long duration = audioTrack.getInfo().length;
                    Long hours = duration / 1000 / 60 / 60;
                    Long minutes = duration / 1000 / 60 % 60;
                    Long seconds = duration / 1000 % 60;
                    Long playTime = 0L;
                    int queue = musicManager.scheduler.queue.size() + 1;
                    if (musicManager.scheduler.queue.size() > 0) {
                        for (AudioTrack track : musicManager.scheduler.queue) {
                            playTime += track.getDuration();
                        }
                        playTime += (musicManager.scheduler.player.getPlayingTrack().getDuration() - musicManager.scheduler.player.getPlayingTrack().getPosition()) - audioTrack.getDuration();
                    }
                    Long playHours = playTime / 1000 / 60 / 60;
                    Long playminutes = playTime / 1000 / 60 % 60;
                    Long playSeconds = playTime / 1000 % 60;
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
                            .setThumbnail(url)
                            .setFooter("Developed by Daly#3068 ❤️", "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                            .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                            .addField("Now", now, true)
                            .addField("Next", next, true)
                            .addField("Approx. time to play", "🕐 " + String.format("%02d:%02d:%02d", playHours, playminutes, playSeconds), true)
                            .addField("Position in Queue", queue + "/" + (musicManager.scheduler.queue.size() + 1), true)
                            .setColor(15844367);
                    textChannel.sendMessageEmbeds(embed.build()).queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                Long length = 0L;
                if (trackUrl.contains("&list")) {
                    for (AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                        length += track.getDuration();
                        track.setUserData((TextChannel) textChannel);
                    }
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("✅ Playlist added to Queue (" + Integer.toString(tracks.size()) + " songs)")
                            .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", length / 1000 / 60 / 60, length / 1000 / 60 % 60, length / 1000 % 60), true)
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️", "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } else if (!tracks.isEmpty()) {
                    musicManager.scheduler.queue(tracks.get(0));
                    tracks.get(0).setUserData((TextChannel) textChannel);
                    URI uri = URI.create(tracks.get(0).getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    Long duration = tracks.get(0).getInfo().length;
                    Long hours = duration / 1000 / 60 / 60;
                    Long minutes = duration / 1000 / 60 % 60;
                    Long seconds = duration / 1000 % 60;
                    Long playTime = 0L;
                    int queue = musicManager.scheduler.queue.size() + 1;
                    if (musicManager.scheduler.queue.size() > 0) {
                        for (AudioTrack track : musicManager.scheduler.queue) {
                            playTime += track.getDuration();
                        }
                        playTime += (musicManager.scheduler.player.getPlayingTrack().getDuration() - musicManager.scheduler.player.getPlayingTrack().getPosition()) - tracks.get(0).getDuration();
                    }
                    Long playHours = playTime / 1000 / 60 / 60;
                    Long playminutes = playTime / 1000 / 60 % 60;
                    Long playSeconds = playTime / 1000 % 60;
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
                    embed.setTitle("🎵 " + tracks.get(0).getInfo().title, tracks.get(0).getInfo().uri)
                            .setAuthor("📀 Adding to queue ")
                            .setDescription(tracks.get(0).getInfo().uri)
                            .setThumbnail(url)
                            .setFooter("Developed by Daly#3068 ❤️", "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                            .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                            .addField("Now", now, true)
                            .addField("Next", next, true)
                            .addField("Approx. time to play", "🕐 " + String.format("%02d:%02d:%02d", playHours, playminutes, playSeconds), true)
                            .addField("Position in Queue", queue + "/" + (musicManager.scheduler.queue.size() + 1), true)
                            .setColor(15844367);
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                String[] strings = trackUrl.split(" ");
                String search = "";
                for (String string : strings) {
                    if (string.equalsIgnoreCase("ytsearch:") || string.equalsIgnoreCase("audio")) {
                        continue;
                    } else {
                        search += " " + string;
                    }
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("⛔ No matches were found for :", search, true).setColor(15844367).setFooter("Developed by Daly#3068 ❤️", "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");

                textChannel.sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ Something happened, Couldn't load track").setColor(15844367).setFooter("Developed by Daly#3068 ❤️", "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");

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
