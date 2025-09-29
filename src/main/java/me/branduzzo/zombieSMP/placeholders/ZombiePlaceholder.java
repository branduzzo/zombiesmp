package me.branduzzo.zombieSMP.placeholders;

import me.branduzzo.zombieSMP.ZombieSMP;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ZombiePlaceholder extends PlaceholderExpansion {

    private final ZombieSMP plugin;

    public ZombiePlaceholder(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "zombies";
    }

    @Override
    public String getAuthor() {
        return "Branduzzo";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("count") || identifier.isEmpty()) {
            return String.valueOf(plugin.getZombieManager().getZombieCount(player));
        }

        return null;
    }
}
