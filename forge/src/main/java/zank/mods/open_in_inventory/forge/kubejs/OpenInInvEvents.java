package zank.mods.open_in_inventory.forge.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author ZZZank
 */
public interface OpenInInvEvents {
    EventGroup GROUP = EventGroup.of("OpenInInvEvents");
    EventHandler REGISTER_ACTION = GROUP.client("registerAction", () -> ActionRegistryEventJS.class);
    EventHandler REGISTER_REPLACE_TEMPLATE = GROUP.client("registerReplaceTemplate", () -> RegisterReplaceTemplateEventJS.class);
}
