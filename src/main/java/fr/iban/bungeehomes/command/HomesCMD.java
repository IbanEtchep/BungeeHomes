package fr.iban.bungeehomes.command;

import fr.iban.bungeehomes.BungeeHomesPlugin;
import fr.iban.bungeehomes.HomeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomesCMD implements CommandExecutor {

    private BungeeHomesPlugin plugin;
    private HomeManager manager;

    public HomesCMD(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getHomeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<String> homeNames = manager.getHomeNames(player.getUniqueId());

            if (!homeNames.isEmpty()) {
                Component component = Component.text("Voici la liste de vos résidences : ", TextColor.fromCSSHexString("#4a69bd"));
                for (String homeName : homeNames) {
                    component = component.append(Component.text(homeName, TextColor.fromCSSHexString("#e58e26")));
                    int index = homeNames.indexOf(homeName);
                    if (index != homeNames.size() - 1) {
                        if (index != homeNames.size() - 2) {
                            component = component.append(Component.text(", ", TextColor.fromCSSHexString("#4a69bd")));
                        } else {
                            component = component.append(Component.text(" et ", TextColor.fromCSSHexString("#4a69bd")));
                        }
                    }
                }
                player.sendMessage(component);
            } else {
                player.sendMessage("§cVous n'avez pas de résidence.");
            }

            if (homeNames.size() >= manager.getMaxHomes(player, 1)) {
                player.sendMessage("§7Vous avez atteint votre nombre de résidence maximal." + (homeNames.size() > 0 ? " Vous en avez " + homeNames.size()+"." : ""));
            } else {
                player.sendMessage("§7Vous pouvez créer jusqu'à " + manager.getMaxHomes(player, 1) + " résidences."
                        + (homeNames.size() > 0 ? " Vous en avez actuellement " + homeNames.size()+"." : ""));
            }
        }
        return false;
    }
}
