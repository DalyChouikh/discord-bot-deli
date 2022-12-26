package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import core.GLA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Skip extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-skip")) {
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
                URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                String videoID = uri.getQuery().split("=")[1];
                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                        "⏭️ Skipped (Requested by " + event.getMember().getUser().getName() + "#"
                                + event.getMember().getUser().getDiscriminator() + ")",
                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setThumbnail(url)
                        .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                musicManager.scheduler.nextTrack();
                if (audioPlayer.getPlayingTrack() != null) {
                    URI urii = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                    String videoIDD = urii.getQuery().split("=")[1];
                    String urll = "http://img.youtube.com/vi/" + videoIDD + "/0.jpg";
                    long duration = audioPlayer.getPlayingTrack().getInfo().length;
                    Long hours = duration / 1000 / 60 / 60;
                    Long minutes = duration / 1000 / 60 % 60;
                    Long seconds = duration / 1000 % 60;
                    String next;
                    if (musicManager.scheduler.queue.peek() == null) {
                        next = ":mute: None";
                    } else {
                        next = "⏩ " + musicManager.scheduler.queue.peek().getInfo().title;
                    }
                    String now;
                    if (musicManager.audioPlayer.isPaused()) {
                        now = "⏸️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                    } else {
                        now = "▶️ " + musicManager.audioPlayer.getPlayingTrack().getInfo().title;
                    }
                    EmbedBuilder embedd = new EmbedBuilder();
                    embedd.setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                            .setAuthor("📀 Now Playing ")
                            .setDescription(audioPlayer.getPlayingTrack().getInfo().uri)
                            .setThumbnail(urll)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png")
                            .addField("Length", "🕐 " + String.format("%02d:%02d:%02d", hours, minutes, seconds), true)
                            .addField("Now", now, true)
                            .addField("Next", next, true)
                            .setColor(15844367);
                    event.getChannel().sendMessageEmbeds(embedd.build()).complete();
                }
            }

        }
    }

    public void onTrackStart() {

    }

}