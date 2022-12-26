package com.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashHelp extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("help")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Deli", null,
                    "https://cdn.discordapp.com/avatars/917074546933260399/0a8290bb752d9f70e2b1af21e017b853.webp?size=80")
                    .setTitle("Commands :")
                    .addField("👂 Want to hear a song?:", "`-play [Song name | URL]` : Plays a given song", false)
                    .addField("❓ Questioning about the author for the current song?:",
                            "`-now` : Displays the current song infos", false)
                    .addField("🧐 Need some concentration?:", "`-pause` : Pauses or resumes a song", false)
                    .addField("👎 Don't like the song?:", "`-skip` : Skips a song", false)
                    .addField("😊 Get some rest:", "`-stop` : Stop the bot from playing music", false)
                    .addField("👄 Some Karaoke?:", "`-lyrics` : Displays the current song lyrics", false)
                    .addField("🧹 Don't want to listen to upcoming song?:",
                            "`-remove [Song position]` : Removes a song from the queue", false)
                    .addField(":face_with_peeking_eye: See what ahead:", "`-queue` : Displays the queue", false)
                    .addField("⌛ Want to know when your favorite song will be played?:",
                            "`-seek [Song position]` : Displays the infos of a given song position in queue", false)
                    .addField("🆓 Feel free to go:",
                            "`-go [b(backward) | f(forward)] [amount in seconds]` : Go forward or backward in the song",
                            false)
                    .setColor(15844367)
                    .setFooter("Developed by Daly#3068 ❤️",
                            "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
            event.deferReply(true).queue();
            event.getHook().sendMessageEmbeds(embed.build()).queue();
            
        }
    }

}
