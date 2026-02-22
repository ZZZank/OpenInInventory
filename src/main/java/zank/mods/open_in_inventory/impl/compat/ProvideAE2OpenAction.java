package zank.mods.open_in_inventory.impl.compat;

import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public class ProvideAE2OpenAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        var helper = new ModSupportHelper(registry);
        if (helper.check("ae2")) {
            helper.tryRegister("wireless_terminal");
            helper.tryRegister("wireless_crafting_terminal");

            helper.tryRegister("certus_quartz_cutting_knife", true);
            helper.tryRegister("nether_quartz_cutting_knife", true);

            helper.tryRegister("portable_item_cell_{ae2_capacity}");
            helper.tryRegister("portable_fluid_cell_{ae2_capacity}");
        }
    }

    @Override
    public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
        registry.put("ae2_capacity", List.of("1k", "4k", "16k", "64k", "256k"));
    }
}
