package com.discord;

import java.util.Arrays;

import com.discord.commands.SlashHelp;
import com.discord.commands.SlashPing;
import com.discord.commands.music.*;
import com.discord.commands.music.contextCmds.*;
import com.discord.events.AutoCompleteGo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
            GatewayIntent.MESSAGE_CONTENT};

    public static void main(String[] args) throws Exception {
        String token = System.getenv("TEST_TOKEN");
        JDA bot = JDABuilder.create(Arrays.asList(INTENTS))
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI)
                .setToken(token)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new SlashPing())
                .addEventListeners(new Play())
                .addEventListeners(new NowPlaying())
                .addEventListeners(new Pause())
                .addEventListeners(new Skip())
                .addEventListeners(new Stop())
                .addEventListeners(new Lyrics())
                .addEventListeners(new Remove())
                .addEventListeners(new Queue())
                .addEventListeners(new Go())
                .addEventListeners(new Seek())
                .addEventListeners(new SlashHelp())
                .addEventListeners(new Resume())
                .addEventListeners(new AutoCompleteGo())
                .addEventListeners(new ContextPlay())
                .addEventListeners(new Clear())
                .addEventListeners(new Shuffle())
                .addEventListeners(new ContextStop())
                .addEventListeners(new ContextSkip())
                .addEventListeners(new ContextPause())
                .addEventListeners(new ContextResume())
                .build();
        bot.getPresence()
                .setActivity(Activity.listening("/help !"));
        bot.updateCommands()
                .addCommands(
                        Commands.slash("help", "Get the list of available commands and their how to use"),
                        Commands.slash("ping", "Check the bot latency"),
                        Commands.slash("play", "Play some music")
                                .addOption(OptionType.STRING, "song", "Plays the song name you provide", true)
                                .setGuildOnly(true),
                        Commands.slash("stop", "Clear the queue and leave the channel").setGuildOnly(true),
                        Commands.slash("now", "Display the current song infos").setGuildOnly(true),
                        Commands.slash("pause", "Pause song").setGuildOnly(true),
                        Commands.slash("resume", "Resume song").setGuildOnly(true),
                        Commands.slash("lyrics", "Request song lyrics").setGuildOnly(true),
                        Commands.slash("skip", "Skip song").setGuildOnly(true),
                        Commands.slash("queue", "Show the first 25 songs in the queue").setGuildOnly(true),
                        Commands.slash("remove", "Remove a song in the queue")
                                .addOption(OptionType.INTEGER, "position","Song position in queue", true)
                                .setGuildOnly(true),
                        Commands.slash("seek", "See song infos in the queue")
                                .addOption(OptionType.INTEGER, "position","Song position in queue", true)
                                .setGuildOnly(true),
                        Commands.slash("go", "Go back or forward in the song")
                                .addOption(OptionType.STRING, "direction","where are you going", true, true)
                                .addOption(OptionType.INTEGER, "position","What second you want to go", true)
                                .setGuildOnly(true),
                        Commands.message("Play this song").setGuildOnly(false),
                        Commands.slash("clear", "Clear the queue").setGuildOnly(true),
                        Commands.slash("shuffle", "Shuffle queue").setGuildOnly(true),
                        Commands.context(Command.Type.USER, "Stop song").setGuildOnly(true),
                        Commands.context(Command.Type.USER, "Skip song").setGuildOnly(true),
                        Commands.context(Command.Type.USER, "Pause song").setGuildOnly(true),
                        Commands.context(Command.Type.USER, "Resume song").setGuildOnly(true)
                )
                .queue();
    }

}
