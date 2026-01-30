package zank.mods.open_in_inventory.impl.crt;

import zank.mods.open_in_inventory.api.OpenActionProvider;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

/**
 * @author ZZZank
 */
public class ProvideCraftTweakerOpenAction implements OpenActionProvider.RequireMod {
    @Override
    public String requiredModId() {
        return "crafttweaker";
    }

    @Override
    public void register(OpenActionRegistry registry) {
        for (var provider : OpenInInventoryCrt.PROVIDERS) {
            provider.accept(registry);
        }
    }
}
