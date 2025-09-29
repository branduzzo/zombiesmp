package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private final ZombieSMP plugin;

    public PlayerJoinLeaveListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getZombieManager().isZombie(event.getPlayer())) {
            plugin.getZombieManager().applyZombieEffects(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (plugin.getZombieManager().isZombie(event.getPlayer())) {
            plugin.getZombieManager().updateOfflineTime(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
}
