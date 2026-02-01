package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public class CommonOpenInInventoryPlugin implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        registry.registerIfPresent(Identifier.tryParse("scannable:scanner"), true);
        registry.registerIfPresent(Identifier.tryParse("extendedcrafting:handheld_table"));
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        /// @see net.minecraft.item.ArmorItem.Type
        /// Due to 1.19 having no `ArmorItem$Type`, we have to provide this manually
        registry.put("armor", List.of("helmet", "chestplate", "leggings", "boots"));

        registry.put("color", Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList());
    }
}
