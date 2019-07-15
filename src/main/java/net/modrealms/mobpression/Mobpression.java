package net.modrealms.mobpression;

import com.google.inject.Inject;
import lombok.Getter;
import net.modrealms.mobpression.config.MainConfiguration;
import net.modrealms.mobpression.data.DatabaseHandler;
import net.modrealms.mobpression.events.CompressionEvents;
import net.modrealms.mobpression.manager.ConfigManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(id = "mobpression", name = "Mobpression", authors = {"TheFlash787"})
public class Mobpression {

    @Inject
    @Getter
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @Getter
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject @Getter
    private Logger logger;

    @Getter
    private EntityManager entityManager;

    @Getter
    private DatabaseHandler databaseHandler;

    private static Mobpression instance;

    @Getter
    private ConfigManager configManager;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        /* Setting the instance */
        instance = this;

        /* Setup Database */
        this.databaseHandler = new DatabaseHandler();

        /* Initialising the entitymanager */
        this.entityManager = new EntityManager();

        /* Initialising the configuration */
        this.configManager = new ConfigManager(configLoader);

        /* Initialising the configuration */
        Sponge.getEventManager().registerListeners(this, new CompressionEvents());

        System.out.println("Ready to start receiving entities!");
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
//        /* Clearing the mob cache for mobs that don't exist */
//        Task.builder().execute(new EntityManager.EntityCheck()).async().submit(Mobpression.getInstance());
    }

    @Listener
    public void onServerStopped(GameStoppedServerEvent event) {
        /* Closing DB Connection */
        this.databaseHandler.close();
    }

    @Listener
    public void onServerReload(GameReloadEvent event) {
        /* Reloading Configuration */
        this.configManager.loadConfiguration();
    }

    public static Mobpression getInstance(){
        return instance;
    }
}
