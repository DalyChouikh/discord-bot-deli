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

public class Resume extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("resume")) {
            AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player;
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).complete();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).complete();
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
                                    "▶️ Resumed by " + event.getMember().getUser().getName() ,
                                    null, event.getMember().getUser().getEffectiveAvatarUrl())
                            .setTitle(audioPlayer.getPlayingTrack().getInfo().title,
                                    audioPlayer.getPlayingTrack().getInfo().uri)
                            .setDescription("** Requested by : ** `" + user.getName() + "`")
                            .setThumbnail(url)
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                    event.replyEmbeds(embed.build()).setEphemeral(false).complete();
                    return;
                } else if (!audioPlayer.isPaused()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("\uD83D\uDEAB Song is playing")
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).complete();
                    return;
                }
            }else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).complete();
            }
        }
    }
}
