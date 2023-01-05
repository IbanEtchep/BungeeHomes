package fr.iban.bungeehomes;

import fr.iban.bungeehomes.command.*;
import fr.iban.bungeehomes.listener.JoinQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BungeeHomesPlugin extends JavaPlugin {

    private static BungeeHomesPlugin instance;
    private HomeManager homeManager;

    @Override
    public void onEnable() {
        instance = this;
        this.homeManager = new HomeManager(this);
        getCommand("bhomes").setExecutor(new HomesCMD(this));
        getCommand("bhome").setExecutor(new HomeCMD(this));
        getCommand("bhome").setTabCompleter(new HomeCMD(this));
        getCommand("bsethome").setExecutor(new SetHomeCMD(this));
        getCommand("bdelhome").setExecutor(new DelHomeCMD(this));
        getCommand("bdelhome").setTabCompleter(new DelHomeCMD(this));
        getCommand("addmaxhomes").setExecutor(new AddMaxHomesCMD());
        getCommand("addmaxbonushomes").setExecutor(new AddMaxBonusHomesCMD());
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public static BungeeHomesPlugin getInstance() {
        return instance;
    }
}
