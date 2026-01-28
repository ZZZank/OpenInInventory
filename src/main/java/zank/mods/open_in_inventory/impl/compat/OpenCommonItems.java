package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.api.OpenActionProvider;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.List;

/**
 * @author ZZZank
 */
public class OpenCommonItems implements OpenActionProvider {
    private static final List<String> ITEM_IDS = List.of(
        "extendedcrafting:handheld_table",
        "patchouli:guide_book",
        "scannable:scanner"
    );

    @Override
    public void register(OpenActionRegistry registry) {
        for (var itemIdString : ITEM_IDS) {
            var item = Registries.ITEM.get(Identifier.tryParse(itemIdString));
            if (item != null) {
                registry.register(item, false);
            }
        }
    }
}
