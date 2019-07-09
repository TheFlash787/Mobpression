package net.modrealms.mobpression;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class EntityManager {

    @Getter
    private HashMap<UUID, Integer> entityMap;

    public EntityManager(){
        this.entityMap = new HashMap<>();
        loadData();
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
}
