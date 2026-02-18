package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.util.DyeColor;
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
        var helper = new ModSupportHelper(registry);
        if (helper.check("scannable")) {
            helper.tryRegister("scanner", true);
        }
        if (helper.check("extendedcrafting")) {
            helper.tryRegister("handheld_table");
        }
        if (helper.check("crafting_on_a_stick")) {
            var items = Arrays.asList(
                "crafting_table",
                "loom",
                "grindstone",
                "cartography_table",
                "stonecutter",
                "smithing_table",
                "anvil",
                "chipped_anvil",
                "damaged_anvil"
            );
            for (var item : items) {
                helper.tryRegister(item);
            }
        }
        if (helper.check("refinedstorage")) {
            var items = Arrays.asList(
                "portable_grid", // RS & RS2
                "creative_portable_grid", // RS & RS2

                "wireless_grid", // RS & RS2
                "creative_wireless_grid", // RS & RS2

                "wireless_fluid_grid", // RS
                "creative_wireless_fluid_grid", // RS

                "wireless_autocrafting_monitor", // RS2
                "creative_wireless_autocrafting_monitor", // RS2

                "wireless_crafting_monitor", // RS
                "creative_wireless_crafting_monitor" // RS
            );
            for (var item : items) {
                helper.tryRegister(item);
            }
        }
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        /// @see net.minecraft.item.ArmorItem.Type
        /// Due to 1.19 having no `ArmorItem$Type`, we have to provide this manually
        registry.put("armor", List.of("helmet", "chestplate", "leggings", "boots"));

        registry.put("color", Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList());
    }
}
