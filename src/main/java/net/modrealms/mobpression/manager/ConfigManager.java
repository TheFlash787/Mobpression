package net.modrealms.mobpression.manager;

import com.google.common.reflect.TypeToken;
import net.modrealms.mobpression.Mobpression;
import net.modrealms.mobpression.config.MainConfiguration;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import java.io.File;

public class ConfigManager {

    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;

    private MainConfiguration mainConfiguration;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader){
        this.configurationLoader = loader;
        loadConfiguration();
    }

    public void loadConfiguration(){
        try{
            File file = new File(Mobpression.getInstance().getConfigDir(), "mobpression.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            configurationLoader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = configurationLoader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            mainConfiguration = config.getValue(TypeToken.of(MainConfiguration.class), new MainConfiguration());
            configurationLoader.save(config);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
