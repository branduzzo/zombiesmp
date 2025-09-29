package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BedExplosionListener implements Listener {

    private final ZombieSMP plugin;

    public BedExplosionListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (!plugin.getConfig().getBoolean("beds_explode")) {
            return;
        }

        event.setCancelled(true);

        Block bed = event.getBed();
        Player player = event.getPlayer();

        bed.getWorld().createExplosion(bed.getLocation(), 4.0f, true, true);

        String message = plugin.getConfig().getString("messages.bed_explosion");
        String prefix = plugin.getConfig().getString("messages.prefix");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    @EventHandler
    public void onBedClick(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("beds_explode")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !isBed(block.getType())) {
            return;
        }

        if (event.getAction().name().contains("RIGHT_CLICK")) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            block.getWorld().createExplosion(block.getLocation(), 4.0f, true, true);

            String message = plugin.getConfig().getString("messages.bed_explosion");
            String prefix = plugin.getConfig().getString("messages.prefix");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        }
    }

    private boolean isBed(Material material) {
        return material.name().contains("BED") && !material.name().contains("BEDROCK");
    }
}
