package zank.mods.open_in_inventory.forge.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

/**
 * @author ZZZank
 */
public class ActionRegistryEventJS extends EventJS {
    public final OpenActionRegistry registry;

    public ActionRegistryEventJS(OpenActionRegistry registry) {
        this.registry = registry;
    }

    public void register(ItemStack stack, boolean sneak) {
        registry.register(stack, sneak);
    }

    public void register(ItemStack stack) {
        registry.register(stack, false);
    }
}
