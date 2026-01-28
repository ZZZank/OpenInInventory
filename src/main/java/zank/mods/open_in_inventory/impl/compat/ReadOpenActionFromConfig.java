package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenActionProvider;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.impl.OpenActionImpl;

/**
 * @author ZZZank
 */
public class ReadOpenActionFromConfig implements OpenActionProvider {

    @Override
    public void register(OpenActionRegistry registry) {
        for (var jsonElement : OpenInInventoryConfig.ENABLED_ITEMS) {
            try {
                var action = OpenInInventory.GSON.fromJson(jsonElement, OpenActionImpl.class);
                registry.register(action.stack(), action.sneak());
            } catch (Exception e) {
                OpenInInventory.LOGGER.error("Error when reading open action from config", e);
            }
        }
    }
}
