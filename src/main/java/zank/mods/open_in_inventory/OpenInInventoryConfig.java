package zank.mods.open_in_inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import zank.mods.open_in_inventory.util.SimpleConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
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

    public static void refresh(Path configFile) throws IOException {
        var cfg = new SimpleConfig();

        cfg.read(OpenInInventory.GSON, configFile);

        cfg.addElementToWrite(
            "//",
            new JsonPrimitive("Refresh config in-game using `/open_in_inventory refresh` command")
        );
        SCREEN_BLACKLIST = OpenInInventory.GSON.fromJson(
            cfg.getJson(
                "screenBlacklist",
                new JsonArray(),
                "Class name of screen in which this mod will be disabled"
            ),
            new TypeToken<>() {}
        );
        REQUIRE_EMPTY_MAIN_HAND = cfg.getBool(
            "requireEmptyMainHand",
            true,
            "Disable Open in Inventory when player is holding something in main hand"
        );
        REQUIRE_SINGLE_STACK = cfg.getBool(
            "requireSingleStack",
            true,
            "Prevent Open in Inventory from applying to item stacks containing more than one item"
        );
        OPEN_DELAY = cfg.getInt("openDelay", 3, "delay (in tick) from swapping item to main hand to use such item");
        DEBUG = cfg.getBool("debug", false, "Debug logging");

        cfg.write(OpenInInventory.GSON, configFile);
    }
}
