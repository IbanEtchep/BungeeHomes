package fr.iban.bungeehomes;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import fr.iban.bukkitcore.utils.SLocationUtils;
import fr.iban.bungeehomes.storage.Storage;
import fr.iban.common.data.sql.DbAccess;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class HomeManager {

    private final BungeeHomesPlugin plugin;
    private final Storage storage = new Storage();
    private final LoadingCache<UUID, List<Home>> homes = Caffeine.newBuilder()
            .build(storage::getHomes);

    public HomeManager(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
    }

    public LoadingCache<UUID, List<Home>> getHomesCache() {
        return homes;
    }

    public List<Home> getHomes(UUID uuid) {
        return homes.get(uuid);
    }

    public Home getHome(UUID uuid, String name) {
        for(Home home : getHomes(uuid)){
            if(home.getName().equalsIgnoreCase(name)){
                return home;
            }
        }
        return null;
    }

    public List<String> getHomeNames(UUID uuid){
        return getHomes(uuid).stream().map(Home::getName).collect(Collectors.toList());
    }

    public void setHome(UUID uuid, Location loc, String name) {
        future(() -> {
            if(getHome(uuid, name) != null){
                try {
                    delHome(uuid, name).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            Home home = new Home(name, SLocationUtils.getSLocation(loc));
            getHomesCache().get(uuid).add(home);
            storage.addHome(uuid, home);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<Void> delHome(UUID uuid, String name){
        return future(() -> {
            Home home = getHome(uuid, name);
            if(home != null) {
                getHomesCache().get(uuid).remove(home);
                storage.deleleHome(uuid, name);
            }
        });
    }

    public int getMaxHomes(Player player, int defaultValue) {
        return getMaxBonusHomes(player, 0) + getMaxRegularHomes(player, defaultValue);
    }

    public int getMaxRegularHomes(Player player, int defaultValue) {
        String permissionPrefix = "bungeehomes.amount.";
        int maxHomes = defaultValue;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            String permission = attachmentInfo.getPermission();
            if (permission.startsWith(permissionPrefix)) {
                int permMaxhomes = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                if(permMaxhomes > maxHomes){
                    maxHomes = permMaxhomes;
                }
            }
        }
        return maxHomes;
    }

    public int getMaxBonusHomes(Player player, int defaultValue) {
        String permissionPrefix = "bungeehomes.bonusamount.";
        int maxHomes = defaultValue;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            String permission = attachmentInfo.getPermission();
            if (permission.startsWith(permissionPrefix)) {
                int permMaxhomes = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                if(permMaxhomes > maxHomes){
                    maxHomes = permMaxhomes;
                }
            }
        }
        return maxHomes;
    }

    private CompletableFuture<Void> future(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw (RuntimeException) e;
            }
        });
    }

}
