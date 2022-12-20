package fr.iban.bungeehomes.command;

import fr.iban.bungeehomes.util.LuckPermsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddMaxBonusHomesCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("bungeehomes.addmaxhomes")){
            if(args.length == 2) {
                Player target = Bukkit.getPlayer(args[0]);
                int amount = Integer.parseInt(args[1]);
                LuckPermsUtils.addBonusHomes(target, amount);
            }
        }
        return false;
    }
}
