package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.resources.ResourceLocation;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;

/**
 * @author ZZZank
 */
public class CommonOpenInInventoryPlugin implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        registry.registerIfPresent(ResourceLocation.tryParse("written_book"));
        registry.registerIfPresent(ResourceLocation.tryParse("writable_book"));

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
        if (helper.check("ae2")) {
            helper.tryRegister("wireless_terminal");
            helper.tryRegister("wireless_crafting_terminal");

            helper.tryRegister("certus_quartz_cutting_knife", true);
            helper.tryRegister("nether_quartz_cutting_knife", true);

            helper.tryRegister("portable_item_cell_{ae2:capacity}");
            helper.tryRegister("portable_fluid_cell_{ae2:capacity}");
        }
        if (helper.check("patchouli")) {
            if (BuiltInRegistries.ITEM.containsKey(helper.id("guide_book"))) {
                var baseItem = BuiltInRegistries.ITEM.get(helper.id("guide_book"));
                BuiltInRegistries.ITEM.stream()
                    .filter(baseItem.getClass()::isInstance)
                    .forEach(registry::register);
            }
        }
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        /// @see net.minecraft.item.ArmorItem.Type
        /// Due to 1.19 having no `ArmorItem$Type`, we have to provide this manually
        registry.put("armor", List.of("helmet", "chestplate", "leggings", "boots"));

        registry.put("color", Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList());

        registry.put("ae2:capacity", List.of("1k", "4k", "16k", "64k", "256k"));
    }
}
