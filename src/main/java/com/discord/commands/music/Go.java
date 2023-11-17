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

public class Go extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("go")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (audioPlayer.getPlayingTrack() != null) {
                if (event.getOption("direction").getAsString().equalsIgnoreCase("backward")) {
                    try {
                        Long amount = event.getOption("position").getAsLong();
                        audioPlayer.getPlayingTrack()
                                .setPosition(audioPlayer.getPlayingTrack().getPosition() - (amount * 1000));
                        URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                        String videoID = uri.getQuery().split("=")[1];
                        String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                        Long l = 0L;
                        l += musicManager.audioPlayer.getPlayingTrack().getPosition();
                        long duration = audioPlayer.getPlayingTrack().getInfo().length;
                        Long hours = duration / 1000 / 60 / 60;
                        Long minutes = duration / 1000 / 60 % 60;
                        Long seconds = duration / 1000 % 60;
                        Long lhours = l / 1000 / 60 / 60;
                        Long lminutes = l / 1000 / 60 % 60;
                        Long lseconds = l / 1000 % 60;
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(
                                "◀️ Went backward " + amount + " seconds (Requested by " + event.getMember().getUser().getName()
                                        + ")",
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                                .setThumbnail(url)
                                .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                                .addField("Played",
                                        "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/"
                                                + String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                        false)
                                .setColor(15844367)
                                .setFooter("Developed by Daly. ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                        event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                        return;
                    } catch (NumberFormatException e) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("⛔ Please enter a valid number")
                                .setColor(15844367)
                                .setFooter("Developed by Daly. ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                        return;
                    }
                }
                else if(event.getOption("direction").getAsString().equalsIgnoreCase("forward")){
                    try {
                        Long amount = event.getOption("position").getAsLong();
                        audioPlayer.getPlayingTrack()
                                .setPosition(audioPlayer.getPlayingTrack().getPosition() + (amount * 1000));
                        URI uri = URI.create(audioPlayer.getPlayingTrack().getInfo().uri);
                        String videoID = uri.getQuery().split("=")[1];
                        String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                        Long l = 0L;
                        l += musicManager.audioPlayer.getPlayingTrack().getPosition();
                        long duration = audioPlayer.getPlayingTrack().getInfo().length;
                        Long hours = duration / 1000 / 60 / 60;
                        Long minutes = duration / 1000 / 60 % 60;
                        Long seconds = duration / 1000 % 60;
                        Long lhours = l / 1000 / 60 / 60;
                        Long lminutes = l / 1000 / 60 % 60;
                        Long lseconds = l / 1000 % 60;
                        Pair<User, TextChannel> pair = (Pair<User, TextChannel>) audioPlayer.getPlayingTrack().getUserData();
                        User user = pair.getFirst();
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(
                                "▶️ Went forward " + amount + " seconds by " + event.getMember().getUser().getName(),
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                                .setThumbnail(url)
                                .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri)
                                .setDescription("** Requested by : ** `"  + "`")
                                .addField("Played",
                                        "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/"
                                                + String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                        false)
                                .setColor(15844367)
                                .setFooter("Developed by Daly. ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                        event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                        return;
                    } catch (NumberFormatException e) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("⛔ Please enter a valid number")
                                .setColor(15844367)
                                .setFooter("Developed by Daly. ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                        return;
                    }
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
        }
    }
}
