package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.List;

/**
 * @author ZZZank
 */
public class ProvideAE2OpenAction implements ModSupportPlugin {
    @Override
    public String requiredModId() {
        return "ae2";
    }

    @Override
    public void registerAction(OpenActionRegistry registry) {
        registry.registerIfPresent(id("wireless_terminal"));
        registry.registerIfPresent(id("wireless_crafting_terminal"));

        registry.registerIfPresent(id("certus_quartz_cutting_knife"), true);
        registry.registerIfPresent(id("nether_quartz_cutting_knife"), true);

        for (var capacity : List.of(1, 4, 16, 64, 256)) {
            registry.registerIfPresent(id("portable_item_cell_" + capacity + "k"));
            registry.registerIfPresent(id("portable_fluid_cell_" + capacity + "k"));
        }
    }
}
