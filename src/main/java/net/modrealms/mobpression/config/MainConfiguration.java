package net.modrealms.mobpression.config;

import com.google.common.reflect.TypeToken;
import lombok.Data;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.EntityTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable @Data
public class MainConfiguration {

    @Setting(value = "general")
    public static MainConfiguration.General general = new MainConfiguration.General();

    @ConfigSerializable @Data
    public static class General {
        @Setting(value = "compression-blacklist", comment = "Adding a mob into this list will prevent it from being compressed")
        public static List<String> compressionBlacklist = new ArrayList<String>() {{
            add(EntityTypes.ENDER_DRAGON.getId());
            add(EntityTypes.WITHER.getId());
        }};
    }
}
