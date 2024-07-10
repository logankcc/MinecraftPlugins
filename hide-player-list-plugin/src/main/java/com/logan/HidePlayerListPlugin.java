package com.logan;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class HidePlayerListPlugin extends JavaPlugin implements Listener {

    private Team hiddenTeam;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        hiddenTeam = scoreboard.getTeam("hiddenTeam");

        if (hiddenTeam == null) {
            hiddenTeam = scoreboard.registerNewTeam("hiddenTeam");
        }

        hiddenTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    @Override
    public void onDisable() {
        if (hiddenTeam != null) {
            hiddenTeam.unregister();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                hiddenTeam.addEntry(event.getPlayer().getName());
            }
        }.runTaskLater(this, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                hiddenTeam.removeEntry(event.getPlayer().getName());
            }
        }.runTaskLater(this, 10L);
    }
}