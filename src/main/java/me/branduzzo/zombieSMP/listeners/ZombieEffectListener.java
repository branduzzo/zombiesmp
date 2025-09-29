package me.branduzzo.zombieSMP.listeners;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;

public class ZombieEffectListener implements Listener {

    private final ZombieSMP plugin;

    public ZombieEffectListener(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionEffectRemove(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getZombieManager().isZombie(player)) {
            return;
        }

        if (event.getModifiedType() == PotionEffectType.SLOWNESS) {
            if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED ||
                    event.getAction() == EntityPotionEffectEvent.Action.CLEARED) {
                event.setCancelled(true);
                plugin.getZombieManager().applyZombieEffects(player);
            }
        }
    }

    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) {
            return;
        }

        Player player = event.getPlayer();

        if (plugin.getZombieManager().isZombie(player)) {
            event.getPlayer().getScheduler().runDelayed(plugin, (task) -> {
                plugin.getZombieManager().applyZombieEffects(player);
            }, null, 1L);
        }
    }
}
