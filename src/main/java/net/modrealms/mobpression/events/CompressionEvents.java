package net.modrealms.mobpression.events;

import net.modrealms.mobpression.EntityManager;
import net.modrealms.mobpression.Mobpression;
import net.modrealms.mobpression.config.MainConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Platform;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompressionEvents {
    private static Mobpression mobpression = Mobpression.getInstance();
    private static final EntityManager entityManager = mobpression.getEntityManager();

    @Listener
    public void onCollideEntity(CollideEntityEvent event, @Root Living sourceEntity){
        /* Check if it's a player, ignore if so */
        if(sourceEntity instanceof Player) return;
        /* Check if the mob's type is in the blacklist */
        if(MainConfiguration.General.whitelistEnabled){
            if(!MainConfiguration.General.compressionBlacklist.contains(sourceEntity.getType().getId())) return;
        } else {
            if(MainConfiguration.General.compressionBlacklist.contains(sourceEntity.getType().getId())) return;
        }
        /* We have a living entity, let's get a list of impacted entities of same type */
        Living master = sourceEntity;
        List<Entity> impactedSimilars = new ArrayList<>();
        for(Entity entity : event.getEntities().stream().filter(e -> e instanceof Living).collect(Collectors.toList())){
            /* Don't bother with players */
            if(entity instanceof Player) continue;
            /* Only look if the entities are of the same type */
            if(sourceEntity.getType() != entity.getType()) continue;
            /* If the entity is in the map, that'll be the new source (master animal) */
            if(entityManager.getEntityMap().containsKey(entity.getUniqueId())){
                if(master.getUniqueId() == sourceEntity.getUniqueId()){
                    /* Only if the master hasn't already switched before */
                    master = (Living) entity;
                    continue;
                }
            }
            impactedSimilars.add(entity);
        }

        /* Now check the configuration for the impacted similars */
        if(MainConfiguration.General.Minimum.enabled){
            if(impactedSimilars.size() < MainConfiguration.General.Minimum.amount){
                return;
            }
        }

        /* Now check the configuration for the impacted similars */
        if(MainConfiguration.General.Maximum.enabled){
            if(entityManager.getEntityMap().getOrDefault(master.getUniqueId(), 1) >= MainConfiguration.General.Maximum.amount){
                return;
            }
        }

        /* Let's delete the impacted entity, and increment the map */
        for(Entity entity : impactedSimilars){
            if(entity.isLoaded()){
                entity.remove();
                int newTotal = entityManager.getEntityMap().getOrDefault(master.getUniqueId(), 1) + entityManager.getEntityMap().getOrDefault(entity.getUniqueId(), 1);
                entityManager.updateEntity(master.getUniqueId(), newTotal);
            }
        }

        /* Now let's update the entity's name */
        if(entityManager.getEntityMap().containsKey(sourceEntity.getUniqueId())){
            updateEntityName(master, entityManager.getEntityMap().get(sourceEntity.getUniqueId()));
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onEntityDamage(DamageEntityEvent event){
        if(event.getTargetEntity() instanceof Living){
            Living sourceEntity = (Living) event.getTargetEntity();
            Location<World> location = sourceEntity.getLocation();
            if(sourceEntity.getType() != EntityTypes.PLAYER){
                if(event.willCauseDeath()){
                    /* It's not a player, continue */
                    if(entityManager.getEntityMap().containsKey(sourceEntity.getUniqueId())){
                        /* If the entity is compressed, decrement the count */
                        entityManager.updateEntity(sourceEntity.getUniqueId(), entityManager.getEntityMap().get(sourceEntity.getUniqueId()) - 1);
                        if(entityManager.getEntityMap().get(sourceEntity.getUniqueId()) > 1){
                            /* Let's kill the entity, and respawn it */
                            int currentAmount = entityManager.getEntityMap().get(sourceEntity.getUniqueId());
                            /* Remove entity from the map */
                            entityManager.removeEntity(sourceEntity.getUniqueId());
                            /* Kill entity */
                            killEntity(sourceEntity);
                            /* Setup the new entity */
                            sourceEntity = (Living) location.createEntity(sourceEntity.getType());
                            /* Spawn the new entity */
                            location.spawnEntity(sourceEntity);
                            /* Update in map */
                            entityManager.updateEntity(sourceEntity.getUniqueId(), currentAmount);
                            /* Update the name */
                            updateEntityName(sourceEntity, entityManager.getEntityMap().get(sourceEntity.getUniqueId()));
                        }
                        else if(entityManager.getEntityMap().get(sourceEntity.getUniqueId()) == 1){
                            /* Let's kill the entity, and respawn it */
                            /* Remove entity from the map */
                            entityManager.removeEntity(sourceEntity.getUniqueId());
                            /* Kill entity */
                            killEntity(sourceEntity);
                            /* Setup the new entity */
                            sourceEntity = (Living) location.createEntity(sourceEntity.getType());
                            /* Spawn the new entity */
                            location.spawnEntity(sourceEntity);
                        }
                        else{
                            /* None left, continue the event, kill the animal */
                            event.setCancelled(false);
                            entityManager.getEntityMap().remove(sourceEntity.getUniqueId());
                        }
                    }
                }
            }
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onEntityDestruct(DestructEntityEvent.Death event){
        if(MainConfiguration.General.disableDeathMessages){
            if(entityManager.getEntityMap().containsKey(event.getTargetEntity().getUniqueId())){
                event.setMessageCancelled(true);
            }
        }
    }

    private void updateEntityName(Living entity, int amount){
        entity.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(MainConfiguration.General.displayName.replace("{compression}", String.valueOf(amount)).replace("{name}", entity.getType().getTranslation().get())));
        entity.offer(Keys.CUSTOM_NAME_VISIBLE, true);
    }

    private void killEntity(Entity entity){
        int delay = 0;
        while(delay == 0) {
            entity.damage(Integer.MAX_VALUE, DamageSources.GENERIC);
            entity.remove();
            delay++;
        }
    }
}
