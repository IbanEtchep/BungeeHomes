package fr.iban.bungeehomes.listener;

import fr.iban.bungeehomes.BungeeHomesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private BungeeHomesPlugin plugin;

    public JoinQuitListener(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        //Mise en cache
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getHomeManager().getHomes().get(player.getUniqueId());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        //Invalidation du cache du joueur.
        plugin.getHomeManager().getHomes().invalidate(player.getUniqueId());
    }
}
