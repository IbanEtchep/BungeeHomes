package fr.iban.bungeehomes.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class LuckPermsUtils {

	private static LuckPerms luckapi = LuckPermsProvider.get();

	public void addPermission(Player player, String perm) {
		User user = loadUser(player);
		PermissionNode node = PermissionNode.builder(perm).build();
		user.getNodes().add(node);
		luckapi.getUserManager().saveUser(user);
	}

	public static void addHomes(Player player, int amount) {
		CompletableFuture<User> userFuture = luckapi.getUserManager().loadUser(player.getUniqueId());
		userFuture.thenAcceptAsync(user -> {
			int homes = 1;
			for (PermissionNode permNode : user.getNodes(NodeType.PERMISSION)) {
				if(permNode.getKey().startsWith("bungeehomes.amount.")) {
					homes = Integer.parseInt(StringUtils.removeStart(permNode.getKey(), "bungeehomes.amount."));
					user.data().remove(permNode);
				}
			}
			Bukkit.getLogger().log(Level.INFO, "Résidences de " + player.getName() + " " + homes + " -> "+(homes+amount));
			user.data().add(Node.builder("bungeehomes.amount."+(homes+amount)).build());
			luckapi.getUserManager().saveUser(user);
		});
	}

	public static void addBonusHomes(Player player, int amount) {
		String perm = "bungeehomes.bonusamount.";
		CompletableFuture<User> userFuture = luckapi.getUserManager().loadUser(player.getUniqueId());
		userFuture.thenAcceptAsync(user -> {
			int homes = 0;
			for (PermissionNode permNode : user.getNodes(NodeType.PERMISSION)) {
				if(permNode.getKey().startsWith(perm)) {
					homes = Integer.parseInt(StringUtils.removeStart(permNode.getKey(), perm));
					user.data().remove(permNode);
				}
			}
			Bukkit.getLogger().log(Level.INFO, "Résidences bonus de " + player.getName() + " " + homes + " -> "+(homes+amount));
			user.data().add(Node.builder(perm+(homes+amount)).build());
			luckapi.getUserManager().saveUser(user);
		});
	}

	public static User loadUser(Player player) {
		if (!player.isOnline())
			throw new IllegalStateException("Player is offline!"); 
		return luckapi.getUserManager().getUser(player.getUniqueId());
	}

}
