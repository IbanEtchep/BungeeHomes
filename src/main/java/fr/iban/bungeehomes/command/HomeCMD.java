package fr.iban.bungeehomes.command;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bungeehomes.BungeeHomesPlugin;
import fr.iban.bungeehomes.Home;
import fr.iban.bungeehomes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeCMD implements CommandExecutor, TabCompleter {

    private final BungeeHomesPlugin plugin;
    private final HomeManager manager;

    public HomeCMD(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getHomeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player)sender;
            UUID uuid = player.getUniqueId();
            String homeName = "home";

            if(args.length == 1){

                String arg = args[0];

                if(player.hasPermission("bungeehomes.home.others") && arg.contains(":")){
                    String[] split = arg.split(":");
                    uuid = Bukkit.getOfflinePlayer(split[0]).getUniqueId();
                    homeName = split[1];
                }else{
                    homeName = arg;
                }
            }

            Home home = manager.getHome(uuid, homeName);

            if(home != null){
                CoreBukkitPlugin.getInstance().getTeleportManager().teleport(player, home.getSLocation(), 3);
            }else{
                player.sendMessage("§cCette résidence n'existe pas.");
            }


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 1) {
                UUID uuid =  player.getUniqueId();
                String prefix = "";
                String arg = args[0];

                if(arg.contains(":")){
                    String[] split = arg.split(":");
                    if(split.length >= 1){
                        uuid = Bukkit.getOfflinePlayer(split[0]).getUniqueId();
                        if(split.length == 2) {
                            arg = split[1];
                        }else{
                            arg = "";
                        }
                        prefix = split[0]+":";
                    }
                }

                List<String> homeNames = manager.getHomeNames(uuid);

                for(String homeName : homeNames){
                    if(homeName.toLowerCase().startsWith(arg.toLowerCase())){
                        suggestions.add(prefix+homeName);
                    }
                }

            }
        }
        return suggestions;
    }
}
