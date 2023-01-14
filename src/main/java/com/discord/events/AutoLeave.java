package com.discord.events;

import com.discord.LavaPlayer.GuildMusicManager;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoLeave {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleLeave(AudioManager audioManager, GuildMusicManager musicManager) {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (audioManager.getConnectedChannel().getMembers().size() == 1 || musicManager.audioPlayer.getPlayingTrack() == null && musicManager.scheduler.queue.isEmpty()) {
                    musicManager.audioPlayer.stopTrack();
                    musicManager.scheduler.queue.clear();
                    audioManager.closeAudioConnection();
                    scheduler.shutdown();
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}
