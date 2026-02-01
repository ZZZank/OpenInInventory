package zank.mods.open_in_inventory.neoforge.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

import java.util.Collection;
import java.util.Map;

/**
 * @author ZZZank
 */
public class RegisterReplaceTemplateEventJS implements KubeEvent {
    public final Map<String, Collection<String>> registry;

    public RegisterReplaceTemplateEventJS(Map<String, Collection<String>> registry) {
        this.registry = registry;
    }

    public void register(String key, Collection<String> replaceWith) {
        registry.put(key, replaceWith);
    }
}
