package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSpawnListener implements Listener {
    private final ZombieSMP plugin;

    public ZombieSpawnListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.ZOMBIE) {
            return;
        }

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        World.Environment environment = event.getLocation().getWorld().getEnvironment();
        if (environment == World.Environment.NORMAL) {
            long worldTime = event.getLocation().getWorld().getTime();
            if (worldTime < 13000 || worldTime > 23000) {
                return;
            }
        }

        double multiplier = plugin.getConfig().getDouble("zombie_spawn_multiplier", 4.0);
        int additionalSpawns = (int) (multiplier - 1);
        if (additionalSpawns <= 0) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < additionalSpawns; i++) {
                    if (Math.random() < 0.8) {
                        event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.ZOMBIE);
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
