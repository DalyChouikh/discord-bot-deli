package com.discord.commands.music;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.swing.JEditorPane;

import com.discord.LavaPlayer.GuildMusicManager;
import com.discord.LavaPlayer.PlayerManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Lyrics extends ListenerAdapter {

        public void onMessageReceived(MessageReceivedEvent event) {
                if (event.getAuthor().isBot()) {
                        return;
                }
                if (!event.isFromGuild()) {
                        return;
                }
                String[] message = event.getMessage().getContentRaw().split(" ");
                if (message[0].equalsIgnoreCase("-lyrics")) {
                        if (!event.getMember().getVoiceState().inAudioChannel()) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor("🔊 You need to join a Voice channel")
                                                .setColor(15844367)
                                                .setFooter("Developed by Daly#3068 ❤️",
                                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                                return;
                        }
                        if (!event.getGuild().getAudioManager().isConnected()) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setAuthor("🔊 I need to join a Voice channel first")
                                                .setTitle("👉 You can use -play [song name/URL]")
                                                .setColor(15844367)
                                                .setFooter("Developed by Daly#3068 ❤️",
                                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                                return;
                        } else {
                                final GuildMusicManager musicManager = PlayerManager.getInstance()
                                                .getMusicManager(event.getGuild());
                                final AudioPlayer audioPlayer = musicManager.audioPlayer;
                                if (audioPlayer.getPlayingTrack() == null) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setAuthor("❌ There is currently no track playing")
                                                        .setColor(15844367)
                                                        .setFooter("Developed by Daly#3068 ❤️",
                                                                        "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                                        return;
                                } else {
                                        String title = audioPlayer.getPlayingTrack().getInfo().title;
                                        title = title.replaceAll("(?i)Official", "").replaceAll("(?i)Music", "")
                                                        .replaceAll("Stream", "").replaceAll("Video", "")
                                                        .replaceAll("\\([^\\(]*\\)|\\[[^\\[]*\\]", "")
                                                        .replaceAll("&", " ").split("(?<!\\w)ft(?!\\w)")[0]
                                                        .split("(?<!\\w)feat(?!\\w)")[0].replaceAll(" ",
                                                                        "%20");
                                        try {
                                                ObjectMapper mapper = new ObjectMapper();
                                                HttpRequest requestId = HttpRequest.newBuilder()
                                                                .uri(URI.create("https://genius-song-lyrics1.p.rapidapi.com/search?q="
                                                                                + title))
                                                                .header("X-RapidAPI-Key", System.getenv("KEY"))
                                                                .header("X-RapidAPI-Host", System.getenv("HOST"))
                                                                .method("GET", HttpRequest.BodyPublishers.noBody())
                                                                .build();
                                                HttpResponse<String> responseId = HttpClient.newHttpClient()
                                                                .send(requestId, HttpResponse.BodyHandlers.ofString());
                                                JsonNode song = mapper.readTree(responseId.body());
                                                JsonNode songId = song.get("hits");
                                                for (JsonNode hit : songId) {
                                                        try {
                                                                HttpRequest requestLyrics = HttpRequest.newBuilder()
                                                                                .uri(URI.create("https://genius-song-lyrics1.p.rapidapi.com/songs/"
                                                                                                + hit.get("result").get(
                                                                                                                "id")
                                                                                                + "/lyrics"))
                                                                                .header("X-RapidAPI-Key",
                                                                                                System.getenv("KEY"))
                                                                                .header("X-RapidAPI-Host",
                                                                                                System.getenv("HOST"))
                                                                                .method("GET", HttpRequest.BodyPublishers
                                                                                                .noBody())
                                                                                .build();
                                                                HttpResponse<String> responseLyrics = HttpClient
                                                                                .newHttpClient().send(requestLyrics,
                                                                                                HttpResponse.BodyHandlers
                                                                                                                .ofString());
                                                                JsonNode lyrics = mapper
                                                                                .readTree(responseLyrics.body());
                                                                JsonNode html = lyrics.get("lyrics")
                                                                                .get("lyrics")
                                                                                .get("body")
                                                                                .get("html");
                                                                JEditorPane editorPane = new JEditorPane();
                                                                editorPane.setContentType("text/plain");
                                                                editorPane.setText(html.asText());
                                                                String text = editorPane.getText();
                                                                URI uri = URI.create(audioPlayer.getPlayingTrack()
                                                                                .getInfo().uri);
                                                                String videoID = uri.getQuery().split("=")[1];
                                                                String url = "http://img.youtube.com/vi/" + videoID
                                                                                + "/0.jpg";
                                                                EmbedBuilder embed = new EmbedBuilder();
                                                                embed.setAuthor(
                                                                                "📀 Now Playing (Lyrics requested by "
                                                                                                + event.getMember()
                                                                                                                .getUser()
                                                                                                                .getName()
                                                                                                + "#"
                                                                                                + event.getMember()
                                                                                                                .getUser()
                                                                                                                .getDiscriminator()
                                                                                                + ")",
                                                                                null,
                                                                                event.getMember().getUser()
                                                                                                .getEffectiveAvatarUrl())
                                                                                .setTitle(title.replaceAll("%20", " "))
                                                                                .setThumbnail(url)
                                                                                .setColor(15844367)
                                                                                .setFooter("Developed by Daly#3068 ❤️",
                                                                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                                                try {
                                                                        embed.setDescription(text);
                                                                } catch (IllegalArgumentException e) {
                                                                        embed.setDescription(
                                                                                        text.substring(0, 4095))
                                                                                        .addField("", text
                                                                                                        .substring(4095, text
                                                                                                                        .length()),
                                                                                                        false);
                                                                }
                                                                event.getChannel().sendMessageEmbeds(embed.build())
                                                                                .queue();
                                                                break;
                                                        } catch (NullPointerException e) {
                                                                continue;
                                                        }
                                                }
                                        } catch (IOException | NullPointerException | InterruptedException e) {
                                                e.printStackTrace();
                                                EmbedBuilder embed = new EmbedBuilder();
                                                embed.setAuthor("❌ No lyrics found")
                                                                .setColor(15844367)
                                                                .setFooter("Developed by Daly#3068 ❤️",
                                                                                "https://cdn.discordapp.com/avatars/392041081983860746/316401c64397974a28995adbe5ee5ed8.png");
                                                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                                        }
                                }
                        }
                }
        }

}
