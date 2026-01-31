package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.List;

/**
 * @author ZZZank
 */
public class ProvideRefinedStorageOpenAction implements ModSupportPlugin {

    @Override
    public String requiredModId() {
        return "refinedstorage";
    }

    @Override
    public void registerAction(OpenActionRegistry registry) {
        var items = List.of(
            "portable_grid", // RS & RS2
            "creative_portable_grid", // RS & RS2

            "wireless_grid", // RS & RS2
            "creative_wireless_grid", // RS & RS2

            "wireless_fluid_grid", // RS
            "creative_wireless_fluid_grid", // RS

            "wireless_autocrafting_monitor", // RS2
            "creative_wireless_autocrafting_monitor", // RS2

            "wireless_crafting_monitor", // RS
            "creative_wireless_crafting_monitor" // RS
        );
        for (var item : items) {
            registry.registerIfPresent(id(item));
        }
    }
}
