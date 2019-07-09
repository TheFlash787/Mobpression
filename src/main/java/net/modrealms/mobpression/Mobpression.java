package net.modrealms.mobpression;

import com.google.inject.Inject;
import lombok.Getter;
import net.modrealms.mobpression.data.DatabaseHandler;
import net.modrealms.mobpression.events.CompressionEvents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "mobpression", name = "Mobpression", version = "1.0.0")
public class Mobpression {

    @Inject
    @Getter
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject @Getter
    private Logger logger;

    @Getter
    private EntityManager entityManager;

    @Getter
    private DatabaseHandler databaseHandler;

    private static Mobpression instance;


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        /* Setting the instance */
        instance = this;

        /* Setup Database */
        this.databaseHandler = new DatabaseHandler();

        /* Initialising the entitymanager */
        this.entityManager = new EntityManager();

        /* Initialising the configuration */
        Sponge.getEventManager().registerListeners(this, new CompressionEvents());

        System.out.println("Ready to start receiving entities!");
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        /* Closing DB Connection */
        this.databaseHandler.close();
    }

    public static Mobpression getInstance(){
        return instance;
    }
}
