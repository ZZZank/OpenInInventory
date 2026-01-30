package zank.mods.open_in_inventory.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import zank.mods.open_in_inventory.OpenInInventory;

/**
 * @author ZZZank
 */
@Mod(OpenInInventory.ID)
public class OpenInInventoryForge extends OpenInInventory {

    public OpenInInventoryForge() {
        EventBuses.registerModEventBus(OpenInInventory.ID, FMLJavaModLoadingContext.get().getModEventBus());
        COMMON = this;
        MinecraftForge.EVENT_BUS.register(new ScreenClosedHandler(actionHandler));
    }
}
