package zank.mods.open_in_inventory.fabric.kubejs;

import zank.mods.open_in_inventory.api.OpenActionProvider;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

/**
 * @author ZZZank
 */
public class ProvideKubeJSAction implements OpenActionProvider.RequireMod {

    @Override
    public String requiredModId() {
        return "kubejs";
    }

    @Override
    public void register(OpenActionRegistry registry) {
        //? if <1.21
        OpenInInvEvents.ACTION_REGISTRY.post(new ActionRegistryEventJS(registry));
    }
}
