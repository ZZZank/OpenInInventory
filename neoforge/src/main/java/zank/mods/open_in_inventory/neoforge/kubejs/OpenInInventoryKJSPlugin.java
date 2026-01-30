package zank.mods.open_in_inventory.neoforge.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;

/**
 * @author ZZZank
 */
public class OpenInInventoryKJSPlugin implements KubeJSPlugin {

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(OpenInInvEvents.GROUP);
    }
}
