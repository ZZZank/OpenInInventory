package zank.mods.open_in_inventory;

import net.fabricmc.api.ModInitializer;

/**
 * @author ZZZank
 */
public class OpenInInventoryFabric implements ModInitializer {
    public static OpenInInventory COMMON;

    @Override
    public void onInitialize() {
        COMMON = new OpenInInventory();
    }
}
