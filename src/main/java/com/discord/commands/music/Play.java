package com.discord.commands.music;


import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Play extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        VoiceChannel connectedChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
        TextChannel channel = (TextChannel) event.getChannel();
        if (event.getName().equalsIgnoreCase("play")) {
            if (event.getOptionsByName("song").isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to provide the Song name")
                        .setTitle("👉 Use /play [song name/URL]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT,
                    Permission.VOICE_SPEAK)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ I either don't have permission to join this channel or to speak")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else{
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection((AudioChannelUnion) connectedChannel);
                String song = event.getOption("song").getAsString();
                if (!isUrl(song)) {
                    song = "ytsearch:" + song + " audio";
                }
                event.deferReply(true).queue();
                event.getHook().editOriginal("\uD83D\uDD0D Searching for **" + song.replaceAll("ytsearch:|audio", "") + "**").queue();
                PlayerManager.getInstance().loadAndPlay(channel, song, event.getUser());
            }
        }

    }

    private boolean isUrl(String link) {
        if(link.contains("youtube.com/watch") || link.contains("youtube.com/playlist")){
            return true;
        }
        else{
            return false;
        }
    }

}
