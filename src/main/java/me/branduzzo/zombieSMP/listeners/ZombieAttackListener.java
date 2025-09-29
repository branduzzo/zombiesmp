package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ZombieAttackListener implements Listener {

    private final ZombieSMP plugin;

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
            plugin.getZombieManager().makeZombie(player, false);
        }
    }

    @EventHandler
    public void onPlayerAttackPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (plugin.getZombieManager().isZombie(attacker) && !plugin.getZombieManager().isZombie(victim)) {
            plugin.getZombieManager().makeZombie(victim, false);
        }
    }
}
