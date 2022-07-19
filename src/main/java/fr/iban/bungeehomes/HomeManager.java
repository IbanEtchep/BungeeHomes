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
    private Storage storage = new Storage();

    private DataSource ds = DbAccess.getDataSource();

    private LoadingCache<UUID, List<Home>> homes = Caffeine.newBuilder()
            .build(uuid -> storage.getHomes((UUID) uuid));

    public HomeManager(BungeeHomesPlugin plugin) {
        this.plugin = plugin;
    }

    public LoadingCache<UUID, List<Home>> getHomes() {
        return homes;
    }

    public Home getHome(UUID uuid, String name) {
        for(Home home : homes.get(uuid)){
            if(home.getName().equalsIgnoreCase(name)){
                return home;
            }
        }
        return null;
    }

    public List<String> getHomeNames(UUID uuid){
        return getHomes().get(uuid).stream().map(home -> home.getName()).collect(Collectors.toList());
    }

    public void setHome(UUID uuid, Location loc, String name) {
        future(() -> {
            if(getHome(uuid, name) != null){
                try {
                    delHome(uuid, name).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            Home home = new Home(name, SLocationUtils.getSLocation(loc));
            getHomes().get(uuid).add(home);
            storage.addHome(uuid, home);
        });
    }

    public CompletableFuture<Void> delHome(UUID uuid, String name){
        return future(() -> {
            Home home = getHome(uuid, name);
            if(home != null) {
                getHomes().get(uuid).remove(home);
                storage.deleleHome(uuid, name);
            }
        });
    }

    public int getMaxHomes(Player player, int defaultValue) {
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

    private CompletableFuture<Void> future(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
    }

}
