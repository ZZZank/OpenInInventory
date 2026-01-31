package zank.mods.open_in_inventory.impl.crt;

import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

/**
 * @author ZZZank
 */
public class ProvideCraftTweakerOpenAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        for (var provider : OpenInInventoryCrt.PROVIDERS) {
            provider.accept(registry);
        }
    }
}
