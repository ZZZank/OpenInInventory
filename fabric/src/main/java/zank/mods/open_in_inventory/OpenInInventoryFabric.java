package zank.mods.open_in_inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @author ZZZank
 */
public class OpenInInventoryFabric extends OpenInInventory implements ModInitializer {

    @Override
    public void onInitialize() {
        COMMON = this;
    }

    @Override
    public String toRuntimeClassName(String className) {
        return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", className);
    }
}
