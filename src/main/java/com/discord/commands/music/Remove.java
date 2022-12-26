package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Remove extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase("-remove")) {
            if (message.length != 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the song number in the queue")
                        .setTitle("👉  You can use -remove [Song position in queue]")
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
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            final AudioPlayer audioPlayer = musicManager.audioPlayer;
            try {
                int queue = Integer.parseInt(message[1]) - 1;
                if(queue == 0){
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("⛔ To remove the current song")
                        .setTitle("👉 You can use -skip")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
                int i = 1;
                if (queue > musicManager.scheduler.queue.size()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ No song found with that position")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else {
                    for (AudioTrack track : musicManager.scheduler.queue) {
                        if (i == queue) {
                            AudioTrack deletedTrack = track;
                            if (musicManager.scheduler.queue.remove(track)) {
                                URI uri = URI.create(track.getInfo().uri);
                                String videoID = uri.getQuery().split("=")[1];
                                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor(
                                        "🧹 Removed from queue (Requested by " + event.getMember().getUser().getName()
                                                + "#"
                                                + event.getMember().getUser().getDiscriminator() + ")",
                                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                                        .setTitle(deletedTrack.getInfo().title)
                                        .setThumbnail(url)
                                        .setColor(15844367)
                                        .setFooter("Developed by Daly#3068 ❤️",
                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                            }
                        }
                        i++;
                    }
                }
            } catch (NumberFormatException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ Please enter a valid number")
                        .setTitle("👉 Use --remove [Song position in queue]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

        }
    }
}
