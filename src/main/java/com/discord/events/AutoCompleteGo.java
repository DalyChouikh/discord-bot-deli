package com.discord.events;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

public class AutoCompleteGo extends ListenerAdapter {
    private String[] direction = new String[]{"backward", "forward"};
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("go") && event.getFocusedOption().getName().equals("direction")) {
            List<Command.Choice> options = Stream.of(direction)
                    .filter((word) -> word.startsWith(event.getFocusedOption().getValue()))
                    .map((word) -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
