package com.discord.commands.music;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Lyrics extends ListenerAdapter {

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("lyrics")) {
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 You need to join a Voice channel")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getAudioManager().isConnected()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("🔊 I need to join a Voice channel first")
                        .setColor(15844367)
                        .setFooter("Developed by Daly. ❤️",
                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                return;
            } else {
                final GuildMusicManager musicManager = PlayerManager.getInstance()
                        .getMusicManager(event.getGuild());
                final AudioPlayer audioPlayer = musicManager.audioPlayer;
                if (audioPlayer.getPlayingTrack() == null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor("❌ There is currently no track playing")
                            .setColor(15844367)
                            .setFooter("Developed by Daly. ❤️",
                                    "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                    return;
                } else {
                    String title = audioPlayer.getPlayingTrack().getInfo().title;
                    title = title.split("\\(|\\[|\\|")[0]
                            .replaceAll("\\([^\\(]*\\)|\\[[^\\[]*\\]", "")
                            .replaceAll("\\|", "")
                            .replaceAll("&", " ").split("(?<!\\w)ft(?!\\w)")[0]
                            .split("(?<!\\w)feat(?!\\w)")[0].replaceAll(" ",
                            "%20");
                    try {
                        event.deferReply(false).queue();
                        HttpRequest requestId = HttpRequest.newBuilder()
                                .uri(URI.create("https://genius-song-lyrics1.p.rapidapi.com/search/?q="
                                        + title))
                                .header("X-RapidAPI-Key", System.getenv("API_KEY"))
                                .header("X-RapidAPI-Host", "genius-song-lyrics1.p.rapidapi.com")
                                .method("GET", HttpRequest.BodyPublishers.noBody())
                                .build();
                        HttpResponse<String> responseId = HttpClient.newHttpClient()
                                .send(requestId, HttpResponse.BodyHandlers.ofString());
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode song = mapper.readTree(responseId.body());
                        List<JsonNode> songsId = song.findValues("id");
                        if (songsId.isEmpty()) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setAuthor("❌ No lyrics are found")
                                    .setColor(15844367)
                                    .setFooter("Developed by Daly. ❤️",
                                            "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                            event.getHook().sendMessageEmbeds(embed.build()).queue();
                            return;
                        }
                        for (JsonNode songId : songsId) {
                            HttpRequest requestLyrics = HttpRequest.newBuilder()
                                    .uri(URI.create("https://genius-song-lyrics1.p.rapidapi.com/song/lyrics/?id=" + songId.asText()))
                                    .header("X-RapidAPI-Key", System.getenv("API_KEY"))
                                    .header("X-RapidAPI-Host", "genius-song-lyrics1.p.rapidapi.com")
                                    .method("GET", HttpRequest.BodyPublishers.noBody())
                                    .build();
                            HttpResponse<String> responseLyrics = HttpClient
                                    .newHttpClient().send(requestLyrics,
                                            HttpResponse.BodyHandlers
                                                    .ofString());
                            JsonNode lyrics = mapper
                                    .readTree(responseLyrics.body());
                            System.out.println(lyrics);
                            JsonNode html = lyrics.findValue("html");
                            System.out.println(html);
                            if (html.isNull()) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor("❌ No lyrics are found")
                                        .setColor(15844367)
                                        .setFooter("Developed by Daly. ❤️",
                                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                                event.getHook().sendMessageEmbeds(embed.build()).queue();
                                return;
                            }
                            if (!html.asText().contains("<h3>") && !html.isNull()) {
                                String lyric = html.asText().replaceAll("<.*?>",
                                        "");
                                URI uri = URI.create(
                                        audioPlayer.getPlayingTrack()
                                                .getInfo().uri);
                                String videoID = uri.getQuery().split("=")[1];
                                String url = "http://img.youtube.com/vi/"
                                        + videoID
                                        + "/0.jpg";
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor(
                                                "📀 Lyrics requested by "
                                                        + event.getMember()
                                                        .getUser()
                                                        .getName(),
                                                null,
                                                event.getMember().getUser()
                                                        .getEffectiveAvatarUrl())
                                        .setTitle(title.replaceAll(
                                                        "%20", " "),
                                                audioPlayer.getPlayingTrack()
                                                        .getInfo().uri)
                                        .setThumbnail(url)
                                        .setColor(15844367)
                                        .setFooter("Developed by Daly. ❤️",
                                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                                try {
                                    embed.setDescription(lyric);
                                } catch (IllegalArgumentException e) {
                                    try {
                                        embed.setDescription(lyric
                                                        .substring(0, 4095))
                                                .addField("", lyric
                                                                .substring(4095, lyric
                                                                        .length()),
                                                        false);
                                    } catch (IllegalArgumentException ex) {
                                        embed.setDescription(lyric
                                                        .substring(0, 4095))
                                                .addField("", lyric
                                                                .substring(4095, 5119),
                                                        false)
                                                .addField("", lyric
                                                                .substring(5119, 6000),
                                                        false);
                                    }
                                }
                                event.getHook().sendMessageEmbeds(embed.build()).queue();
                                break;
                            }
                            if(songId.equals(songsId.get(songsId.size()-1))){
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor("❌ No lyrics are found")
                                        .setColor(15844367)
                                        .setFooter("Developed by Daly. ❤️",
                                                "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                                event.getHook().sendMessageEmbeds(embed.build()).queue();
                                return;
                            }
                            else {
                                System.out.println("else");
                                continue;
                            }
                        }
                    } catch (IOException | InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor("❌ Couldn't fetch lyrics")
                                .setColor(15844367)
                                .setFooter("Developed by Daly#3068 ❤️",
                                        "https://cdn.discordapp.com/avatars/392041081983860746/57fd83084f10579392e5fbb0dc6bbf7c.png");
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                }
            }
        }
    }
}
