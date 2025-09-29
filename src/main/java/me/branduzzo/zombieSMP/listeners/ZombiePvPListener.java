package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class ZombiePvPListener implements Listener {

    private final ZombieSMP plugin;

    public ZombiePvPListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieTargetPlayer(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Zombie) || !(event.getTarget() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getTarget();

        if (plugin.getZombieManager().isZombie(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttackZombie(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Zombie)) {
            return;
        }

        Player player = (Player) event.getDamager();

        if (plugin.getZombieManager().isZombie(player)) {
            event.setCancelled(true);

            String message = plugin.getConfig().getString("messages.zombie_attack_immune");
            String prefix = plugin.getConfig().getString("messages.prefix");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        }
    }

    @EventHandler
    public void onZombieAttackZombiePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Zombie) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (plugin.getZombieManager().isZombie(player)) {
            event.setCancelled(true);
        }
    }
}
