package com.discord.commands.music.contextCmds;

import com.discord.LavaPlayer.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class ContextPlay extends ListenerAdapter {
    
    public void onMessageContextInteraction(MessageContextInteractionEvent event){
        if(event.getName().equals("Play this song")){
            VoiceChannel connectedChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            TextChannel channel = (TextChannel) event.getChannel();
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ I either don't have permission to join this channel or to speak")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }else if(!event.getTarget().getContentRaw().contains("youtube.com/watch") && event.getTarget().getContentRaw().length() > 70){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("⛔ This message is too long to be a title or an URL link for a song")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            else{
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection((AudioChannelUnion) connectedChannel);
                String song = event.getTarget().getContentRaw();
                if (!isUrl(song)) {
                    song = "ytsearch:" + song + " audio";
                }
                event.deferReply(true).complete();
                event.getHook().editOriginal("\uD83D\uDD0D Searching for **" + song.replaceAll("ytsearch:|audio", "") + "**").complete();
                PlayerManager.getInstance().loadAndPlay(channel, song, event.getUser());
            }
        }
    }

    private boolean isUrl(String link) {
        if(link.contains("youtube.com/watch")){
            return true;
        }
        else{
            return false;
        }
    }

}
