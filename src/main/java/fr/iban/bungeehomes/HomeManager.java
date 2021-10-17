package fr.iban.bungeehomes;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import fr.iban.bukkitcore.utils.SLocationUtils;
import fr.iban.bungeehomes.storage.Storage;
import fr.iban.common.data.sql.DbAccess;
import org.bukkit.Location;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
        if(getHome(uuid, name) != null){
            delHome(uuid, name);
        }
        Home home = new Home(name, SLocationUtils.getSLocation(loc));
        getHomes().get(uuid).add(home);
        future(() -> {
            storage.addHome(uuid, home);
        });
    }

    public void delHome(UUID uuid, String name){
        Home home = getHome(uuid, name);
        if(home != null) {
            getHomes().get(uuid).remove(home);
            future(() -> {
                storage.deleleHome(uuid, name);
            });
        }
    }

    private <T> CompletableFuture<T> future(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
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
