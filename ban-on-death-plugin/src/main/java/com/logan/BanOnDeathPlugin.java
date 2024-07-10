package com.logan;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

public class BanOnDeathPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerProfile playerProfile = event.getEntity().getPlayerProfile();
        BanList<PlayerProfile> banList = Bukkit.getBanList(BanList.Type.PROFILE);
        banList.addBan(playerProfile, "You died!", (java.time.Duration) null, "Console");
        event.getEntity().kickPlayer("You died!");
    }
}