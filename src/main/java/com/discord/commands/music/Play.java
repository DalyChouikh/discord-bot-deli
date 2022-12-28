package com.discord.commands.music;

import java.net.URI;
import java.net.URISyntaxException;

import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Play extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String[] content = message.getContentRaw().split(" ");
        VoiceChannel connectedChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
        TextChannel channel = (TextChannel) event.getChannel();
        if (event.getMember() != null) {
            if (content.length == 1 && content[0].equalsIgnoreCase("-play")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 To use the play command properly")
                        .setTitle("👉 Use -play [song name/URL]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            } else if (content[0].equalsIgnoreCase("--play") && !event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel first")
                        .setTitle("👉  You can use -play [song name/URL]")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT,
                    Permission.VOICE_SPEAK)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ I either don't have permission to join this channel or to speak")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            } else if (connectedChannel != null && content[0].equalsIgnoreCase("-play")) {
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection((AudioChannelUnion) connectedChannel);
                String link = "";
                for (String string : content) {
                    if (string.equalsIgnoreCase("-play")) {
                        continue;
                    }
                    link += " " + string;
                }
                if (!isUrl(link)) {
                    link = "ytsearch:" + link + " audio";
                } else {
                    link = content[1].split("&")[0];
                }

                PlayerManager.getInstance().loadAndPlay(channel, link);

            }
        }
        // Timer timer = new Timer();
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // System.out.println("ping");
        // if (event.getGuild().getAudioManager().isConnected() &&
        // !event.getGuild().getAudioManager().getSendingHandler().canProvide()) {
        // System.out.println("pong");
        // event.getGuild().getAudioManager().closeAudioConnection();
        // }
        // }
        // }, 60000);

    }

    private boolean isUrl(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
