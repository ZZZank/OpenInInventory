package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.api.OpenActionRegistry;

/**
 * @author ZZZank
 */
public class CraftingOnAStickCompat implements ModSupportPlugin {
    @Override
    public String requiredModId() {
        return "crafting_on_a_stick";
    }

    @Override
    public void registerAction(OpenActionRegistry registry) {
        registry.registerIfPresent(id("crafting_table"));
        registry.registerIfPresent(id("loom"));
        registry.registerIfPresent(id("grindstone"));
        registry.registerIfPresent(id("cartography_table"));
        registry.registerIfPresent(id("stonecutter"));
        registry.registerIfPresent(id("smithing_table"));
        registry.registerIfPresent(id("anvil"));
        registry.registerIfPresent(id("chipped_anvil"));
        registry.registerIfPresent(id("damaged_anvil"));
    }
}
