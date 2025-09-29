package me.branduzzo.zombieSMP;

import me.branduzzo.zombieSMP.commands.ZombiesCommand;
import me.branduzzo.zombieSMP.listeners.*;
import me.branduzzo.zombieSMP.managers.ZombieManager;
import me.branduzzo.zombieSMP.placeholders.ZombiePlaceholder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ZombieSMP extends JavaPlugin {

    private static ZombieSMP instance;
    private LuckPerms luckPerms;
    private ZombieManager zombieManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        if (!setupLuckPerms()) {
            getLogger().severe("LuckPerms not found! Plugin disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        zombieManager = new ZombieManager(this);

        registerCommands();
        registerListeners();
        setupPlaceholders();

        getLogger().info("ZombieSMP has been enabled!");
    }

    @Override
    public void onDisable() {
        if (zombieManager != null) {
            zombieManager.saveData();
        }
        getLogger().info("ZombieSMP has been disabled!");
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return true;
        }
        return false;
    }

    private void registerCommands() {
        getCommand("zombies").setExecutor(new ZombiesCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ZombieAttackListener(this), this);
        getServer().getPluginManager().registerEvents(new BedExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombiePvPListener(this), this);
    }

    private void setupPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ZombiePlaceholder(this).register();
            getLogger().info("PlaceholderAPI hooked!");
        }
    }

    public static ZombieSMP getInstance() {
        return instance;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public ZombieManager getZombieManager() {
        return zombieManager;
    }
}
