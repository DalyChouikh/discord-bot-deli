package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Go extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-go")) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            if (message.length != 3) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 To use the go command properly")
                        .setTitle("👉 Use -go [b(backward) | f(forward)] [amount in seconds]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
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
            if (audioPlayer.getPlayingTrack() != null) {
                if (message[1].equalsIgnoreCase("b")) {
                    try {
                        Long amount = Long.parseLong(message[2]);
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
                                        + "#"
                                        + event.getMember().getUser().getDiscriminator() + ")",
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                                .setThumbnail(url)
                                .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                                .addField("Played",
                                        "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/"
                                                + String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                        false)
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    } catch (NumberFormatException e) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("⛔ Please enter a valid number")
                                .setTitle("👉 Use --go [b(backward) | f(forward)] [amount in seconds]")
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                }
                else if(message[1].equalsIgnoreCase("f")){
                    try {
                        Long amount = Long.parseLong(message[2]);
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
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(
                                "▶️ Went forward " + amount + " seconds (Requested by " + event.getMember().getUser().getName()
                                        + "#"
                                        + event.getMember().getUser().getDiscriminator() + ")",
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                                .setThumbnail(url)
                                .setTitle("🎵 " + audioPlayer.getPlayingTrack().getInfo().title)
                                .addField("Played",
                                        "🕐 " + String.format("%02d:%02d:%02d", lhours, lminutes, lseconds) + "/"
                                                + String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                        false)
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    } catch (NumberFormatException e) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("⛔ Please enter a valid number")
                                .setTitle("👉 Use --go [b(backward) | f(forward)] [amount in seconds]")
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("❌ There is currently no track playing")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
        }
    }
}
