package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.registry.Registries;
import zank.mods.open_in_inventory.api.OpenActionProvider;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

/**
 * @author ZZZank
 */
public class ProvidePatchouliBookOpenAction implements OpenActionProvider.RequireMod {
    @Override
    public String requiredModId() {
        return "patchouli";
    }

    @Override
    public void register(OpenActionRegistry registry) {
        var action = registry.registerIfPresent(id("guide_book"), false);
        if (action.isPresent()) {
            var item0 = action.get().stack().getItem();
            var type = item0.getClass();
            for (var item : Registries.ITEM) {
                if (type.isInstance(item) && item != item0) {
                    registry.register(item, false);
                }
            }
        }
    }
}
