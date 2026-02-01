package zank.mods.open_in_inventory.impl.crt;

import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

import java.util.Collection;
import java.util.Map;

/**
 * @author ZZZank
 */
public class ProvideCraftTweakerOpenAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        for (var provider : OpenInInventoryCrt.ACTION_PROVIDERS.view()) {
            provider.accept(registry);
        }
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        for (var handler : OpenInInventoryCrt.REPLACE_TEMPLATE_PROVIDERS.view()) {
            handler.accept(registry);
        }
    }
}
