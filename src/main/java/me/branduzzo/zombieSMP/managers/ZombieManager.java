package me.branduzzo.zombieSMP.managers;

import me.branduzzo.zombieSMP.ZombieSMP;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZombieManager {
    private final ZombieSMP plugin;
    private final Map<UUID, ZombieData> zombies;
    private final Map<UUID, Integer> zombieCount;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public ZombieManager(ZombieSMP plugin) {
        this.plugin = plugin;
        this.zombies = new ConcurrentHashMap<>();
        this.zombieCount = new ConcurrentHashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "zombiedata.yml");
        loadData();
        startZombieTask();
    }

    public void makeZombie(Player player, boolean byStaff) {
        if (isZombie(player)) return;

        String previousGroup = getPrimaryGroup(player);
        long endTime = System.currentTimeMillis() + (plugin.getConfig().getLong("zombie_duration_minutes") * 60000);
        ZombieData data = new ZombieData(previousGroup, endTime, System.currentTimeMillis());
        zombies.put(player.getUniqueId(), data);
        zombieCount.put(player.getUniqueId(), zombieCount.getOrDefault(player.getUniqueId(), 0) + 1);

        String zombieCommand = plugin.getConfig().getString("zombie_command").replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), zombieCommand);

        applyZombieEffects(player);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lZOMBIEE"),
                ChatColor.translateAlternateColorCodes('&', "&7You have been infected!"),
                10, 70, 20);

        String message = byStaff
                ? plugin.getConfig().getString("messages.zombie_added").replace("{player}", player.getName())
                : plugin.getConfig().getString("messages.zombie_infected");
        sendMessage(player, message);

        saveData();
    }

    public void cureZombie(Player player) {
        ZombieData data = zombies.remove(player.getUniqueId());
        if (data == null) return;

        removeZombieEffects(player);

        String cureCommand = plugin.getConfig().getString("cure_command")
                .replace("{player}", player.getName())
                .replace("{previous_group}", data.getPreviousGroup());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cureCommand);

        sendMessage(player, plugin.getConfig().getString("messages.zombie_cured"));
        saveData();
    }

    public void cureZombie(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            cureZombie(player);
        } else {
            ZombieData data = zombies.remove(playerId);
            if (data != null) {
                String playerName = Bukkit.getOfflinePlayer(playerId).getName();
                String cureCommand = plugin.getConfig().getString("cure_command")
                        .replace("{player}", playerName)
                        .replace("{previous_group}", data.getPreviousGroup());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cureCommand);
                saveData();
            }
        }
    }

    public boolean isZombie(Player player) {
        return zombies.containsKey(player.getUniqueId());
    }

    public boolean isZombie(UUID playerId) {
        return zombies.containsKey(playerId);
    }

    public int getZombieCount(Player player) {
        return zombieCount.getOrDefault(player.getUniqueId(), 0);
    }

    public void applyZombieEffects(Player player) {
        int slownessLevel = Math.min(3, Math.max(1, plugin.getConfig().getInt("slowness_level", 1)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, slownessLevel - 1, false, false, false));
    }

    public void removeZombieEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
    }

    public void updateOfflineTime(UUID playerId, long currentTime) {
        ZombieData data = zombies.get(playerId);
        if (data != null && plugin.getConfig().getBoolean("count_offline_time")) {
            data.setLastSeen(currentTime);
        }
    }

    private String getPrimaryGroup(Player player) {
        try {
            User user = plugin.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);
            return user.getPrimaryGroup();
        } catch (Exception e) {
            return "default";
        }
    }

    private void sendMessage(Player player, String message) {
        String prefix = plugin.getConfig().getString("messages.prefix");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    private void startZombieTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                List<UUID> toRemove = new ArrayList<>();

                for (Map.Entry<UUID, ZombieData> entry : zombies.entrySet()) {
                    UUID playerId = entry.getKey();
                    ZombieData data = entry.getValue();

                    if (plugin.getConfig().getBoolean("count_offline_time")) {
                        if (currentTime >= data.getEndTime()) {
                            toRemove.add(playerId);
                        }
                    } else {
                        Player player = Bukkit.getPlayer(playerId);
                        if (player != null) {
                            long onlineTime = currentTime - data.getLastSeen();
                            if (data.getAccumulatedTime() + onlineTime >= (plugin.getConfig().getLong("zombie_duration_minutes") * 60000)) {
                                toRemove.add(playerId);
                            } else {
                                data.addAccumulatedTime(onlineTime);
                                data.setLastSeen(currentTime);
                            }
                        }
                    }
                }

                for (UUID playerId : toRemove) {
                    cureZombie(playerId);
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isZombie(player)) {
                        applyZombieEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create zombie data file!");
                return;
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.contains("zombies")) {
            for (String uuidString : dataConfig.getConfigurationSection("zombies").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                String previousGroup = dataConfig.getString("zombies." + uuidString + ".previousGroup");
                long endTime = dataConfig.getLong("zombies." + uuidString + ".endTime");
                long lastSeen = dataConfig.getLong("zombies." + uuidString + ".lastSeen");
                long accumulatedTime = dataConfig.getLong("zombies." + uuidString + ".accumulatedTime");
                zombies.put(uuid, new ZombieData(previousGroup, endTime, lastSeen, accumulatedTime));
            }
        }

        if (dataConfig.contains("counts")) {
            for (String uuidString : dataConfig.getConfigurationSection("counts").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                int count = dataConfig.getInt("counts." + uuidString);
                zombieCount.put(uuid, count);
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, ZombieData> entry : zombies.entrySet()) {
            String uuidString = entry.getKey().toString();
            ZombieData data = entry.getValue();
            dataConfig.set("zombies." + uuidString + ".previousGroup", data.getPreviousGroup());
            dataConfig.set("zombies." + uuidString + ".endTime", data.getEndTime());
            dataConfig.set("zombies." + uuidString + ".lastSeen", data.getLastSeen());
            dataConfig.set("zombies." + uuidString + ".accumulatedTime", data.getAccumulatedTime());
        }

        for (Map.Entry<UUID, Integer> entry : zombieCount.entrySet()) {
            dataConfig.set("counts." + entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save zombie data!");
        }
    }

    public static class ZombieData {
        private String previousGroup;
        private long endTime;
        private long lastSeen;
        private long accumulatedTime;

        public ZombieData(String previousGroup, long endTime, long lastSeen) {
            this.previousGroup = previousGroup;
            this.endTime = endTime;
            this.lastSeen = lastSeen;
            this.accumulatedTime = 0;
        }

        public ZombieData(String previousGroup, long endTime, long lastSeen, long accumulatedTime) {
            this.previousGroup = previousGroup;
            this.endTime = endTime;
            this.lastSeen = lastSeen;
            this.accumulatedTime = accumulatedTime;
        }

        public String getPreviousGroup() { return previousGroup; }
        public long getEndTime() { return endTime; }
        public long getLastSeen() { return lastSeen; }
        public long getAccumulatedTime() { return accumulatedTime; }
        public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
        public void addAccumulatedTime(long time) { this.accumulatedTime += time; }
    }
}
