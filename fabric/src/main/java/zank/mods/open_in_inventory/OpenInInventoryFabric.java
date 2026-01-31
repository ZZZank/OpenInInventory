package zank.mods.open_in_inventory;

import dev.architectury.platform.Platform;
import net.fabricmc.api.ModInitializer;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.fabric.kubejs.ProvideKubeJSAction;

import java.util.List;

/**
 * @author ZZZank
 */
public class OpenInInventoryFabric extends OpenInInventory implements ModInitializer {

    @Override
    public void onInitialize() {
        COMMON = this;
    }

    @Override
    protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
        super.registerPlugin(plugins);
        if (Platform.isModLoaded("kubejs")) {
            plugins.add(new ProvideKubeJSAction());
        }
    }
}
