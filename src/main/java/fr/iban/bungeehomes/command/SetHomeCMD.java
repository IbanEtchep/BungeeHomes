package fr.iban.bungeehomes.command;

import fr.iban.bukkitcore.menu.ConfirmMenu;
import fr.iban.bungeehomes.BungeeHomesPlugin;
import fr.iban.bungeehomes.Home;
import fr.iban.bungeehomes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SetHomeCMD implements CommandExecutor {

    private BungeeHomesPlugin plugin;
    private HomeManager manager;

    public SetHomeCMD(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getHomeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.broadcastMessage("a");

        if(sender instanceof Player){
            Player player = (Player)sender;
            UUID uuid = player.getUniqueId();
            String homeName = "home";

            if(args.length == 1){

                String arg = args[0];

                if(arg.contains(":")){
                    String[] split = arg.split(":");
                    uuid = Bukkit.getOfflinePlayer(split[0]).getUniqueId();
                    homeName = split[1];
                }else{
                    homeName = arg;
                }

            }
            Bukkit.broadcastMessage("a");
            Home home = manager.getHome(uuid, homeName);
            Bukkit.broadcastMessage("b");

            if(home != null){
                UUID finalUuid = uuid;
                String finalHomeName = homeName;
                new ConfirmMenu(player, "§c§lÉcraser une résidence?", "Cette résidence existe déjà, voulez-vous l'écraser?", confirmed -> {
                    if(confirmed){
                        player.sendMessage("§aLa résidence a bien été créée à l'endroit où vous vous trouvez.");
                        manager.setHome(finalUuid, player.getLocation(), finalHomeName);
                    }else{
                        player.sendMessage("§cAction annulée.");
                    }
                }).open();
            }else{
                player.sendMessage("§aLa résidence a bien été créée à l'endroit où vous vous trouvez.");
                manager.setHome(uuid, player.getLocation(), homeName);
            }


        }
        return false;
    }

}
