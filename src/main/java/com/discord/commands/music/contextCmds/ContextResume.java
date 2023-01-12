package com.discord.commands.music.contextCmds;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.net.URI;

public class ContextResume extends ListenerAdapter {
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("Resume song")) {
            AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player;
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
            if (audioPlayer.getPlayingTrack() != null) {
                if (audioPlayer.isPaused()) {
                    audioPlayer.setPaused(false);
                    GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
                    URI uri = URI.create(audioPlayer
                            .getPlayingTrack().getInfo().uri);
                    String videoID = uri.getQuery().split("=")[1];
                    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                    Pair<User, TextChannel> pair = (Pair<User, TextChannel>) PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player
                            .getPlayingTrack().getUserData();
                    User user = pair.getFirst();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                                    "▶️ Resumed by " + event.getMember().getUser().getName() + "#"
                                            + event.getMember().getUser().getDiscriminator(),
                                    null, event.getMember().getUser().getEffectiveAvatarUrl())
                            .setTitle(audioPlayer.getPlayingTrack().getInfo().title,
                                    audioPlayer.getPlayingTrack().getInfo().uri)
                            .setDescription("** Requested by : ** `" + user.getName() + "#" + user.getDiscriminator() + "`")
                            .setThumbnail(url)
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                    return;
                } else if (!audioPlayer.isPaused()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("\uD83D\uDEAB Song is playing")
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
            }
        }
    }
}
