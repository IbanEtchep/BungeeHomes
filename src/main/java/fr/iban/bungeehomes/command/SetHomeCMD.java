package fr.iban.bungeehomes.command;

import fr.iban.bukkitcore.menu.ConfirmMenu;
import fr.iban.bukkitcore.utils.SLocationUtils;
import fr.iban.bungeehomes.BungeeHomesPlugin;
import fr.iban.bungeehomes.Home;
import fr.iban.bungeehomes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SetHomeCMD implements CommandExecutor {

    private final HomeManager manager;

    public SetHomeCMD(BungeeHomesPlugin plugin) {
        this.manager = plugin.getHomeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            String homeName = "home";

            if (args.length == 1) {

                String arg = args[0];

                if (player.hasPermission("bungeehomes.sethome.others") && arg.contains(":")) {
                    String[] split = arg.split(":");
                    uuid = Bukkit.getOfflinePlayer(split[0]).getUniqueId();
                    homeName = split[1];
                } else {
                    homeName = arg;
                }
            }

            if(homeName.equalsIgnoreCase("bed")) {
                player.sendMessage("§cCe nom de résidence est réservé. Veuillez en choisir un autre.");
                return false;
            }

            Home home = manager.getHome(uuid, homeName);

            if (home != null) {
                UUID finalUuid = uuid;
                String finalHomeName = homeName;
                new ConfirmMenu(player, "§c§lÉcraser une résidence?", "Cette résidence existe déjà, voulez-vous l'écraser?", confirmed -> {
                    if (confirmed) {
                        if (SLocationUtils.isSafeLocation(player.getLocation())) {
                            player.sendMessage("§aLa résidence a bien été créée à l'endroit où vous vous trouvez.");
                            manager.setHome(finalUuid, player.getLocation(), finalHomeName);
                        } else {
                            player.sendMessage("§cVous ne pouvez pas créer une résidence ici.");
                        }
                    } else {
                        player.sendMessage("§cAction annulée.");
                    }
                    player.closeInventory();
                }).open();
            } else if (player.hasPermission("bungeehomes.amount.unlimited")
                    || manager.getMaxHomes(player, 1) > manager.getHomes(uuid).size()) {

                if (SLocationUtils.isSafeLocation(player.getLocation())) {
                    player.sendMessage("§aLa résidence a bien été créée à l'endroit où vous vous trouvez.");
                    manager.setHome(uuid, player.getLocation(), homeName);
                } else {
                    player.sendMessage("§cVous ne pouvez pas créer une résidence ici.");
                }
            } else {
                player.sendMessage("§cVous avez atteint la limite de résidences que vous pouvez posséder.");
            }

        }
        return false;
    }


}
