package com.discord.commands.music.contextCmds;

import com.discord.Bot;
import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public class ContextSkip extends ListenerAdapter {
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getName().equals("Skip song")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }else if (audioPlayer.getPlayingTrack() == null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else {
                URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                String videoID = uri.getQuery().split("=")[1];
                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                Pair<User, TextChannel> pair = (Pair<User, TextChannel>) audioPlayer.getPlayingTrack().getUserData();
                User user = pair.getFirst();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                                "⏭️ Skipped  by " + event.getMember().getUser().getName(),
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setDescription("** Requested by : ** `" + user.getName() + "`")
                        .setThumbnail(url)
                        .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                musicManager.scheduler.nextTrack();
            }
        }
    }
}
