package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

        for (var capacity : registry.getReplaceTemplate("ae2_capacity")) {
            registry.registerIfPresent(id("portable_item_cell_" + capacity));
            registry.registerIfPresent(id("portable_fluid_cell_" + capacity));
        }
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        registry.put("ae2_capacity", List.of("1k", "4k", "16k", "64k", "256k"));
    }
}
