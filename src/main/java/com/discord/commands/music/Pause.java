package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.PlayerManager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Pause extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("pause")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player;
            if (audioPlayer.getPlayingTrack() != null) {
                if (!audioPlayer.isPaused()) {
                    audioPlayer.setPaused(true);
                    URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    Pair<User, TextChannel> pair = (Pair<User, TextChannel>) audioPlayer.getPlayingTrack().getUserData();
                    User user = pair.getFirst();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                                    "⏸️ Paused by " + event.getMember().getUser().getName() + "#"
                                            + event.getMember().getUser().getDiscriminator(),
                                    null, event.getMember().getUser().getEffectiveAvatarUrl())
                            .setTitle(audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                            .setDescription("** Requested by : ** `" + user.getName() + "#" + user.getDiscriminator() + "`")
                            .setThumbnail(url)
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                    return;
                }else if(audioPlayer.isPaused()){
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("\uD83D\uDEAB Song is paused")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                }
            }else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
        }
    }
}
