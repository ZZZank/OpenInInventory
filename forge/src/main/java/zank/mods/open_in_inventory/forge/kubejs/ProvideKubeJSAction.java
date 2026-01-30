package zank.mods.open_in_inventory.forge.kubejs;

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
        OpenInInvEvents.ACTION_REGISTRY.post(new ActionRegistryEventJS(registry));
    }
}
