package com.discord.commands.music;

import java.net.URI;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Remove extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("remove")) {
            if (event.getOptionsByName("position").isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the song number in the queue")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
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
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            try {
                int queue = event.getOption("position").getAsInt() - 1;
                if(queue == 0){
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("⛔ To remove the current song")
                        .setTitle("👉 use /skip")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                }
                int i = 1;
                if (queue > musicManager.scheduler.queue.size()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ No song found with that position")
                            .setColor(15844367)
                            .setFooter("Developed by Daly#3068 ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else {
                    for (AudioTrack track : musicManager.scheduler.queue) {
                        if (i == queue) {
                            AudioTrack deletedTrack = track;
                            if (musicManager.scheduler.queue.remove(track)) {
                                URI uri = URI.create(track.getInfo().uri);
                                String videoID = uri.getQuery().split("=")[1];
                                String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
                                Pair<User, TextChannel> pair = (Pair<User, TextChannel>) deletedTrack.getUserData();
                                User user = pair.getFirst();
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor(
                                        "🧹 Removed from queue by " + event.getMember().getUser().getName()
                                                + "#"
                                                + event.getMember().getUser().getDiscriminator(),
                                        null, event.getMember().getUser().getEffectiveAvatarUrl())
                                        .setTitle(deletedTrack.getInfo().title, deletedTrack.getInfo().uri)
                                        .setDescription("** Requested by : ** `" + user.getName() + "#" + user.getDiscriminator() + "`")
                                        .setThumbnail(url)
                                        .setColor(15844367)
                                        .setFooter("Developed by Daly#3068 ❤️",
                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                                return;
                            }
                        }
                        i++;
                    }
                }
            } catch (NumberFormatException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ Please enter a valid number")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }

        }
    }
}
