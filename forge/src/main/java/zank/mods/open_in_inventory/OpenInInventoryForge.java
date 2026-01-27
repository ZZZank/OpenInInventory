package zank.mods.open_in_inventory;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author ZZZank
 */
@Mod(OpenInInventory.ID)
public class OpenInInventoryForge {
    public static OpenInInventory COMMON;

    public OpenInInventoryForge() {
        EventBuses.registerModEventBus(OpenInInventory.ID, FMLJavaModLoadingContext.get().getModEventBus());
        COMMON = new OpenInInventory();
    }
}
