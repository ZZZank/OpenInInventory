package zank.mods.open_in_inventory.fabric.kubejs;

import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

/**
 * @author ZZZank
 */
public class ProvideKubeJSAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        //? if <1.21
        OpenInInvEvents.ACTION_REGISTRY.post(new ActionRegistryEventJS(registry));
    }
}
