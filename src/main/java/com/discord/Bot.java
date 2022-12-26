package com.discord;

import java.util.Arrays;
import com.discord.commands.music.*;
import com.discord.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
      public final static GatewayIntent[] INTENTS = { GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES,
                  GatewayIntent.GUILD_MESSAGE_REACTIONS,
                  GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES,
                  GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
                  GatewayIntent.MESSAGE_CONTENT };

      public static void main(String[] args) throws Exception {
            String token = System.getenv("TOKEN");
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
                        .build();
            bot.getPresence()
                        .setActivity(Activity.listening("/help!"));
            bot.updateCommands()
                        .addCommands(Commands.slash("help", "Get the list of available commands and their how to use"),
                        Commands.slash("ping", "Check the bot latency"))
                        .queue();
      }

}
