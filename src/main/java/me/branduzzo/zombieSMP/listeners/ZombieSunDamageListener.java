package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSunDamageListener implements Listener {
    private final ZombieSMP plugin;

    public ZombieSunDamageListener(ZombieSMP plugin) {
        this.plugin = plugin;
        startSunDamageTask();
    }

    private void startSunDamageTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (!plugin.getZombieManager().isZombie(player)) {
                        continue;
                    }

                    if (player.getWorld().getTime() < 0 || player.getWorld().getTime() > 12300) {
                        continue;
                    }

                    if (player.getLocation().getBlock().getLightFromSky() < 15) {
                        continue;
                    }

                    ItemStack helmet = player.getInventory().getHelmet();
                    if (helmet != null && helmet.getType() != Material.AIR) {
                        continue;
                    }

                    player.damage(1.0);
                    player.setFireTicks(20);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
