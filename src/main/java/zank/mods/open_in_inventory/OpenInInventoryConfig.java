package zank.mods.open_in_inventory;

import com.demonwav.mcdev.annotations.Translatable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import zank.mods.open_in_inventory.util.SimpleConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ZZZank
 */
public abstract class OpenInInventoryConfig {
    public static Set<String> SCREEN_BLACKLIST = new HashSet<>();
    public static boolean REQUIRE_EMPTY_MAIN_HAND = true;
    public static boolean REQUIRE_SINGLE_STACK = true;
    /// in ticks
    public static int OPEN_DELAY = 3;
    public static boolean DEBUG = false;
    public static List<OpenAction> ENABLED_ITEMS = new ArrayList<>();

    public static void refresh(Path configFile) throws IOException {
        var cfg = new SimpleConfig();

        cfg.read(OpenInInventory.GSON, configFile);

        cfg.addElementToWrite(
            "//",
            new JsonPrimitive("Refresh config in-game using `/open_in_inventory refresh` command")
        );
        SCREEN_BLACKLIST = OpenInInventory.GSON.fromJson(
            getEntry(cfg, "screen_blacklist", new JsonArray()),
            new TypeToken<>() {}
        );
        REQUIRE_EMPTY_MAIN_HAND = getEntry(cfg, "require_empty_main_hand", true);
        REQUIRE_SINGLE_STACK = getEntry(cfg, "require_single_stack", true);
        OPEN_DELAY = getEntry(cfg, "open_delay", 3);
        DEBUG = getEntry(cfg, "debug", false);
        ENABLED_ITEMS = OpenInInventory.GSON.fromJson(
            getEntry(cfg, "enabled_items", new JsonArray()),
            new TypeToken<>() {}
        );

        cfg.write(OpenInInventory.GSON, configFile);
    }

    private static JsonElement getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        JsonElement fallback
    ) {
        return cfg.getJson(
            key,
            fallback,
            Text.translatable(OpenInInventory.ID + ".config." + key).getString()
        );
    }

    private static int getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        int fallback
    ) {
        return cfg.getInt(
            key,
            fallback,
            Text.translatable(OpenInInventory.ID + ".config." + key).getString()
        );
    }

    private static boolean getEntry(
        SimpleConfig cfg,
        @Translatable(prefix = OpenInInventory.ID + ".config.") String key,
        boolean fallback
    ) {
        return cfg.getBool(
            key,
            fallback,
            Text.translatable(OpenInInventory.ID + ".config." + key).getString()
        );
    }
}
