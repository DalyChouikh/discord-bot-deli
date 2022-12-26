package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Pause extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-pause")) {
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
            if (PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.isPaused()) {
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.setPaused(false);
                URI uri = URI.create(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                        .getPlayingTrack().getInfo().uri);
                String videoID = uri.getQuery().split("=")[1];
                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                        "▶️ Resumed (by " + event.getMember().getUser().getName() + "#"
                                + event.getMember().getUser().getDiscriminator() + ")",
                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setTitle(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                                .getPlayingTrack().getInfo().title)
                        .setThumbnail(url)
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else if (!PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.isPaused()) {
                if (PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                        .getPlayingTrack() != null) {
                    PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.setPaused(true);
                    URI uri = URI.create(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                            .getPlayingTrack().getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                            "⏸️ Paused (Requested by " + event.getMember().getUser().getName() + "#"
                                    + event.getMember().getUser().getDiscriminator() + ")",
                            null, event.getMember().getUser().getEffectiveAvatarUrl())
                            .setTitle(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                                    .getPlayingTrack().getInfo().title)
                            .setThumbnail(url)
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ There is currently no track playing")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            }
        }
    }
}
