package fr.iban.bungeehomes;

import fr.iban.bungeehomes.command.HomeCMD;
import fr.iban.bungeehomes.command.SetHomeCMD;
import fr.iban.bungeehomes.listener.JoinQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BungeeHomesPlugin extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        this.homeManager = new HomeManager(this);
        getCommand("home").setExecutor(new HomeCMD(this));
        getCommand("home").setTabCompleter(new HomeCMD(this));
        getCommand("sethome").setExecutor(new SetHomeCMD(this));
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
