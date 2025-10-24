package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZombieAttackListener implements Listener {
    private final ZombieSMP plugin;
    private final Map<UUID, Integer> playerHitCount = new HashMap<>();

    public ZombieAttackListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (plugin.getZombieManager().isZombie(player)) {
            return;
        }

        if (player.getWorld().getTime() >= 13000 && player.getWorld().getTime() <= 23000) {
            int hitsRequired = plugin.getConfig().getInt("hits_required_to_transform", 2);
            int currentHits = playerHitCount.getOrDefault(player.getUniqueId(), 0) + 1;

            if (currentHits >= hitsRequired) {
                event.setDamage(0);
                playerHitCount.remove(player.getUniqueId());
                plugin.getZombieManager().makeZombie(player, false);
            } else {
                playerHitCount.put(player.getUniqueId(), currentHits);
            }
        }
    }

    @EventHandler
    public void onPlayerAttackPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (plugin.getZombieManager().isZombie(attacker) && plugin.getZombieManager().isZombie(victim)) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getZombieManager().isZombie(attacker) && !plugin.getZombieManager().isZombie(victim)) {
            int hitsRequired = plugin.getConfig().getInt("hits_required_to_transform", 2);
            int currentHits = playerHitCount.getOrDefault(victim.getUniqueId(), 0) + 1;

            if (currentHits >= hitsRequired) {
                event.setDamage(0);
                playerHitCount.remove(victim.getUniqueId());
                plugin.getZombieManager().makeZombie(victim, false, attacker);
            } else {
                playerHitCount.put(victim.getUniqueId(), currentHits);
            }
        }
    }
}
