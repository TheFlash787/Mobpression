package net.modrealms.mobpression;

import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EntityManager {

    @Getter
    public HashMap<UUID, Integer> entityMap;

    public EntityManager(){
        this.entityMap = new HashMap<>();
        loadData();
        /* Run the entitycheck task */
//        Task.builder().execute(new EntityCheck()).interval(1, TimeUnit.MINUTES).delay(5, TimeUnit.SECONDS).submit(Mobpression.getInstance());
    }

    public void updateEntity(UUID uuid, int amount){
        /* Add entity to the list */
        this.entityMap.put(uuid, amount);
        Mobpression.getInstance().getDatabaseHandler().updateEntity(uuid, amount);
    }

    public void removeEntity(UUID uuid){
        this.entityMap.remove(uuid);
        Mobpression.getInstance().getDatabaseHandler().deleteEntity(uuid);
    }

    public void loadData(){
        Mobpression.getInstance().getDatabaseHandler().getEntities().forEach((uuid, amount) -> {
            this.entityMap.put(uuid, amount);
        });
    }

    public static class EntityCheck implements Runnable {

        @Override
        public void run() {
            for(Map.Entry<UUID, Integer> entry : Mobpression.getInstance().getEntityManager().getEntityMap().entrySet()){
                if(!existsInServer(entry.getKey())){
                    Mobpression.getInstance().getEntityManager().removeEntity(entry.getKey());
                }
            }
        }

        private boolean existsInServer(UUID uuid){
            for(World world : Sponge.getServer().getWorlds()){
                if(world.getEntity(uuid).isPresent()){
                    return true;
                }
            }
            return false;
        }
    }
}
