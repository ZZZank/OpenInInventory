package zank.mods.open_in_inventory;

import com.demonwav.mcdev.annotations.Translatable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import zank.mods.open_in_inventory.util.SimpleConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author ZZZank
 */
public abstract class OpenInInventoryConfig {
    public static Set<String> SCREEN_BLACKLIST;
    public static boolean REQUIRE_EMPTY_MAIN_HAND;
    public static boolean REQUIRE_SINGLE_STACK;
    public static int OPEN_DELAY;
    public static boolean DEBUG;
    public static JsonArray ENABLED_ITEMS;

    public static void refresh(Path configFile) throws IOException {
        var cfg = new SimpleConfig();

        cfg.read(OpenInInventory.GSON, configFile);

        cfg.addElementToWrite(
            "//",
            new JsonPrimitive(Text.translatable("open_in_inventory.config.refresh").getString())
        );
        var _screenBlackList = OpenInInventory.GSON.toJsonTree(Set.of(
            // for some reason, creative inventory will eat your item
            "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen",
            "net.minecraft.class_481"
        ));
        SCREEN_BLACKLIST = OpenInInventory.GSON.fromJson(
            getEntry(cfg, "screen_blacklist", _screenBlackList),
            new TypeToken<Set<String>>() {}.getType()
        );
        REQUIRE_EMPTY_MAIN_HAND = getEntry(cfg, "require_empty_main_hand", true);
        REQUIRE_SINGLE_STACK = getEntry(cfg, "require_single_stack", true);
        OPEN_DELAY = getEntry(cfg, "open_delay", 3);
        DEBUG = getEntry(cfg, "debug", false);
        ENABLED_ITEMS = getEntry(cfg, "enabled_items", new JsonArray()).getAsJsonArray();

        cfg.write(OpenInInventory.GSON, configFile);
    }

    private static JsonElement getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        JsonElement fallback
    ) {
        return cfg.getJson(key, fallback, I18n.translate(OpenInInventory.ID + ".config." + key));
    }

    private static int getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        int fallback
    ) {
        return cfg.getInt(key, fallback, I18n.translate(OpenInInventory.ID + ".config." + key));
    }

    private static boolean getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        boolean fallback
    ) {
        return cfg.getBool(key, fallback, I18n.translate(OpenInInventory.ID + ".config." + key));
    }
}
