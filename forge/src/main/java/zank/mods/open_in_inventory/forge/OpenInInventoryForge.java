package zank.mods.open_in_inventory.forge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.forge.kubejs.KubeJSOpenInInventoryPlugin;

import java.util.List;

/**
 * @author ZZZank
 */
@Mod(OpenInInventory.ID)
public class OpenInInventoryForge extends OpenInInventory {

    public OpenInInventoryForge() {
        EventBuses.registerModEventBus(OpenInInventory.ID, FMLJavaModLoadingContext.get().getModEventBus());
        COMMON = this;
    }

    @Override
    protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
        super.registerPlugin(plugins);
        if (Platform.isModLoaded("kubejs")) {
            plugins.add(new KubeJSOpenInInventoryPlugin());
        }
    }
}
