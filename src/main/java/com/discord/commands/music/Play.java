package com.discord.commands.music;


import com.discord.Bot;
import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.internal.entities.channel.concrete.StageChannelImpl;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Play extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("play")) {
            AudioChannel connectedChannel = event.getMember().getVoiceState().getChannel();
            TextChannel channel = (TextChannel) event.getChannel();
            if (event.getOptionsByName("song").isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the Song name")
                        .setTitle("👉 Use /play [song name/URL]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else if (!event.getGuild().getSelfMember().hasPermission(connectedChannel, Permission.VOICE_CONNECT,
                    Permission.VOICE_SPEAK)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ I either don't have permission to join this channel or to speak")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                Bot.bot.getUsersByName("daly.ch", true).get(0).getAvatarUrl());
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else{
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(connectedChannel);

                String song = event.getOption("song").getAsString();
                if (!isUrl(song)) {
                    song = "ytmsearch:" + song  ;
                }
                event.deferReply(true).complete();
                event.getHook().editOriginal("\uD83D\uDD0D Searching for **" + song.replaceAll("[a-zA-Z]*search:|audio", "") + "**")
                        .complete();
                PlayerManager.getInstance().loadAndPlay(channel, song, event.getUser());
            }
        }

    }

    private boolean isUrl(String link) {
        return (link.contains("youtube.com/watch") || link.contains("youtube.com/playlist")) && URI.create(link).getScheme() != null;
    }

}
