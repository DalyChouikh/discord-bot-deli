package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NowPlaying extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("now")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (audioPlayer.getPlayingTrack() == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
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
                Pair<User, TextChannel> pair = (Pair<User, TextChannel>) audioPlayer.getPlayingTrack().getUserData();
                User user = pair.getFirst();
                embed.setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                        .setAuthor("📀 Now Playing ")
                        .setDescription("** Requested by : ** `" + user.getName() +  "`")
                        .setThumbnail(url)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png")
                        .addField("Played", "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/" + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                        .addField("Now", now, true)
                        .addField("Next", next, true)
                        .setColor(15844367);
                event.replyEmbeds(embed.build()).setEphemeral(false).queue();
            }
        }
    }

}
