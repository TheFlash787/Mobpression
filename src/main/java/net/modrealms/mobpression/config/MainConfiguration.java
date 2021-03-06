package net.modrealms.mobpression.config;

import com.google.common.reflect.TypeToken;
import lombok.Data;
import lombok.Setter;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.EntityTypes;
import sun.applet.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MainConfiguration {

    @Setting(value = "general")
    public static MainConfiguration.General general = new MainConfiguration.General();

    @Setting(value = "compression-minimum")
    public static MainConfiguration.General.Minimum minimum = new MainConfiguration.General.Minimum();

    @Setting(value = "compression-maximum")
    public static MainConfiguration.General.Maximum maximum = new MainConfiguration.General.Maximum();

    @ConfigSerializable
    public static class General {
        @Setting(value = "blacklist-into-whitelist", comment = "Turning this on will reverse the blacklist (into a whitelist) and only allow the mobs you specify to compress")
        public static boolean whitelistEnabled = false;

        @Setting(value = "disable-all-death-messages", comment = "Turning this on will override the gamerule showDeathMessages and disable all compressed-mob death messages from showing.")
        public static boolean disableDeathMessages = true;

        @Setting(value = "display-name", comment = "Use this to choose what the name above the head will look like")
        public static String displayName = "&6&l[&d{compression}x&6&l] &f{name}";

        @Setting(value = "compression-blacklist", comment = "Adding a mob into this list will prevent it from being compressed")
        public static List<String> compressionBlacklist = new ArrayList<String>() {{
            add(EntityTypes.ENDER_DRAGON.getId());
            add(EntityTypes.WITHER.getId());
        }};

        @ConfigSerializable
        public static class Minimum {

            @Setting(value = "enabled", comment = "Having this enabled will only compress the mobs when there are a certain amount of like-mobs touching at one time.")
            public static boolean enabled = false;

            @Setting(value = "amount")
            public static int amount = 2;
        }

        @ConfigSerializable
        public static class Maximum {

            @Setting(value = "enabled", comment = "Having this enabled will only allow mobs to compress to a certain point")
            public static boolean enabled = false;

            @Setting(value = "amount")
            public static int amount = 10;
        }
    }
}
