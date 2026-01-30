package zank.mods.open_in_inventory.forge.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;

/**
 * @author ZZZank
 */
public class OpenInInventoryKJSPlugin extends KubeJSPlugin {

    @Override
    public void registerEvents() {
        OpenInInvEvents.GROUP.register();
    }
}
