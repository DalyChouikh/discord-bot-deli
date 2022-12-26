package com.discord.commands.music;

import java.io.IOException;
import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import core.GLA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Lyrics extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-lyrics")) {
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
            } else {
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
                    String title = audioPlayer.getPlayingTrack().getInfo().title;
                    title = title.replaceAll("(?i)Official", "").replaceAll("(?i)Music", "")
                            .replaceAll("(?i)Video", "").replaceAll("\\(|\\)", "")
                            .replaceAll("\\[|\\]", "").replaceAll("(?i)Audio", "")
                            .replaceAll("(?i)Lyrics|Lyric", "").replaceAll("(?i)Clip", "");
                    GLA gla = new GLA();
                    try {
                        String lyrics = !gla
                                .search(title)
                                .getHits().isEmpty()
                                        ? gla.search(title).getHits().peekFirst().fetchLyrics()
                                        : "";
                        if (lyrics.isEmpty()) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setAuthor(
                                    "⛔ No lyrics were found (Requested by " + event.getMember().getUser().getName()
                                            + "#" + event.getMember().getUser().getDiscriminator() + ")")
                                    .setColor(15844367)
                                    .setFooter("Developed by Daly#3068 ❤️",
                                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();
                            return;
                        } else {
                            URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                            String videoID = uri.getQuery().split("=")[1];
                            String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setAuthor(
                                    "📀 Now Playing (Lyrics requested by " + event.getMember().getUser().getName() + "#"
                                            + event.getMember().getUser().getDiscriminator() + ")",
                                    null, event.getMember().getUser().getEffectiveAvatarUrl())
                                    .setTitle(title)
                                    .setThumbnail(url)
                                    .setColor(15844367)
                                    .setFooter("Developed by Daly#3068 ❤️",
                                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                            try {
                                embed.setDescription(lyrics);

                            } catch (IllegalArgumentException e) {
                                System.out.println("Code went here");
                                embed.setDescription(lyrics.substring(0, 4095))
                                        .addField("", lyrics.substring(4095, lyrics.length()), false);

                            }
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        }
                    } catch (IOException | NullPointerException e) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("🚫 An Error Occurred, Please try again")
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    }
                }
            }
        }
    }

}
