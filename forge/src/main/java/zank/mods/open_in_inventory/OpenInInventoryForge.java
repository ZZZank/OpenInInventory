package zank.mods.open_in_inventory;

import cpw.mods.modlauncher.api.INameMappingService;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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
    public String toRuntimeClassName(String className) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, className);
    }
}
