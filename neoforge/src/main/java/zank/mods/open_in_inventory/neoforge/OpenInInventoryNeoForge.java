

package zank.mods.open_in_inventory.neoforge;

import net.neoforged.fml.common.Mod;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.neoforge.kubejs.ProvideKubeJSAction;

import java.util.List;

@Mod(OpenInInventory.ID)
public class OpenInInventoryNeoForge extends OpenInInventory {
    public OpenInInventoryNeoForge() {
        COMMON = this;
    }

    @Override
    protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
        super.registerPlugin(plugins);
        plugins.add(new ProvideKubeJSAction());
    }
}
