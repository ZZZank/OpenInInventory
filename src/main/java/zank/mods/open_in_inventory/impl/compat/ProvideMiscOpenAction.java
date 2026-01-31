package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

import java.util.List;

/**
 * @author ZZZank
 */
public class ProvideMiscOpenAction implements OpenInInventoryPlugin {
    private static final List<String> ITEM_IDS = List.of(
        "extendedcrafting:handheld_table"
    );

    @Override
    public void registerAction(OpenActionRegistry registry) {
        registry.registerIfPresent(Identifier.tryParse("scannable:scanner"), true);
        for (var itemIdString : ITEM_IDS) {
            registry.registerIfPresent(Identifier.tryParse(itemIdString));
        }
    }
}
