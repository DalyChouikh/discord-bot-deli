package com.discord;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import com.discord.commands.Help;
import com.discord.commands.SlashHelp;
import com.discord.commands.music.Go;
import com.discord.commands.music.Lyrics;
import com.discord.commands.music.NowPlaying;
import com.discord.commands.music.Pause;
import com.discord.commands.music.Play;
import com.discord.commands.music.Queue;
import com.discord.commands.music.Remove;
import com.discord.commands.music.Seek;
import com.discord.commands.music.Skip;
import com.discord.commands.music.Stop;
import com.discord.commands.trivia.Trivia;

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
                        .addEventListeners(new MyListener())
                        .addEventListeners(new Play())
                        .addEventListeners(new Trivia())
                        .addEventListeners(new NowPlaying())
                        .addEventListeners(new Pause())
                        .addEventListeners(new Skip())
                        .addEventListeners(new Stop())
                        .addEventListeners(new Lyrics())
                        .addEventListeners(new Help())
                        .addEventListeners(new Remove())
                        .addEventListeners(new Queue())
                        .addEventListeners(new Go())
                        .addEventListeners(new Seek())
                        .addEventListeners(new SlashHelp())
                        .build();
            bot.getPresence()
                        .setActivity(Activity.listening("/help!"));
            bot.updateCommands()
                        .addCommands(Commands.slash("help", "Get the list of available commands and their how to use"))
                        .queue();
            // String endpoint = "https://opentdb.com/api.php?amount=10";

            // // Make the HTTP GET request to the API endpoint
            // HttpClient client = HttpClientBuilder.create().build();
            // HttpGet request = new HttpGet(endpoint);
            // HttpResponse response = client.execute(request);

            // // Get the response body as a string
            // String jsonString = EntityUtils.toString(response.getEntity());

            // // Parse the JSON string into a JSONObject
            // JSONObject json = new JSONObject(jsonString);

            // // Get the array of questions from the JSONObject
            // JSONArray jsonQuestions = json.getJSONArray("results");

            // // Convert the JSONArray into a list of Question objects
            // List<Question> questions = new ArrayList<>();
            // for (int i = 0; i < jsonQuestions.length(); i++) {
            // JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);
            // String question = jsonQuestion.getString("question");
            // questions.add(new Question(question));
            // }
            // System.out.println(json);
            // // Print the questions to the console
            // for (Question question : questions) {
            // System.out.println(question.getQuestion());
            // }
      }

}
