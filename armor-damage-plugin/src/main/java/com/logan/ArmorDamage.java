package com.logan;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorDamage extends JavaPlugin implements Listener {

    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (BukkitRunnable task : playerTasks.values()) {
            task.cancel();
        }
        playerTasks.clear();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().runTaskLater(this, () -> handleArmorCheck(player), 20L);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().runTaskLater(this, () -> handleArmorCheck(player), 20L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (isArmor(player.getInventory().getItemInMainHand())) {
                Bukkit.getScheduler().runTaskLater(this, () -> handleArmorCheck(player), 20L);
            }
        }
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Bukkit.getScheduler().runTaskLater(this, () -> handleArmorCheck(player), 20L);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, () -> handleArmorCheck(player), 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cancelArmorDamageTask(player);
    }

    private void handleArmorCheck(Player player) {
        if (isWearingArmor(player)) {
            if (!playerTasks.containsKey(player.getUniqueId())) {
                applyArmorDamage(player);
            }
        } else {
            cancelArmorDamageTask(player);
        }
    }

    private boolean isWearingArmor(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (isArmor(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArmor(ItemStack item) {
        if (item == null)
            return false;
        String typeName = item.getType().name();
        return typeName.endsWith("_HELMET") || typeName.endsWith("_CHESTPLATE") ||
                typeName.endsWith("_LEGGINGS") || typeName.endsWith("_BOOTS");
    }

    private void applyArmorDamage(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (isWearingArmor(player)) {
                    player.damage(2.0);
                } else {
                    cancelArmorDamageTask(player);
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(this, 0L, 20L);
        playerTasks.put(player.getUniqueId(), task);
    }

    private void cancelArmorDamageTask(Player player) {
        UUID playerId = player.getUniqueId();
        if (playerTasks.containsKey(playerId)) {
            playerTasks.get(playerId).cancel();
            playerTasks.remove(playerId);
        }
    }
}
