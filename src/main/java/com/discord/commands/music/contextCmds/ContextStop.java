package com.discord.commands.music.contextCmds;

import com.discord.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ContextStop extends ListenerAdapter {

    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getName().equals("Stop song")) {
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
                embed.setAuthor("🔊 I need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else {
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.stopTrack();
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.setPaused(false);
                PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.clear();
                event.getGuild().getAudioManager().closeAudioConnection();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                                "⏹️ Stopped by " + event.getMember().getUser().getName() + "#"
                                        + event.getMember().getUser().getDiscriminator(),
                                null, event.getMember().getUser().getEffectiveAvatarUrl())
                        .setTitle("🧹 Queue cleared")
                        .setColor(15844367)
                        .setFooter("Developed by Daly#3068 ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                event.replyEmbeds(embed.build()).setEphemeral(false).queue();
                return;
            }
        }
    }
}

