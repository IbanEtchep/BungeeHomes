package fr.iban.bungeehomes;

import fr.iban.bungeehomes.command.*;
import fr.iban.bungeehomes.listener.JoinQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BungeeHomesPlugin extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        this.homeManager = new HomeManager(this);
        getCommand("bhomes").setExecutor(new HomesCMD(this));
        getCommand("bhome").setExecutor(new HomeCMD(this));
        getCommand("bhome").setTabCompleter(new HomeCMD(this));
        getCommand("bsethome").setExecutor(new SetHomeCMD(this));
        getCommand("bdelhome").setExecutor(new DelHomeCMD(this));
        getCommand("bdelhome").setTabCompleter(new DelHomeCMD(this));
        getCommand("addmaxhomes").setExecutor(new AddMaxHomesCMD());
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
}
