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
        "patchouli:guide_book"
    );

    @Override
    public void register(OpenActionRegistry registry) {
        registry.registerIfPresent(Identifier.tryParse("scannable:scanner"), true);
        for (var itemIdString : ITEM_IDS) {
            registry.registerIfPresent(Identifier.tryParse(itemIdString), false);
        }
    }
}
