package zank.mods.open_in_inventory.neoforge.kubejs;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author ZZZank
 */
public interface OpenInInvEvents {
    EventGroup GROUP = EventGroup.of("OpenInInvEvents");
    EventHandler ACTION_REGISTRY = GROUP.client("actionRegistry", () -> ActionRegistryEventJS.class);
}
