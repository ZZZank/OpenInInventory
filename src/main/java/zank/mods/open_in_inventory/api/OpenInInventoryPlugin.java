package zank.mods.open_in_inventory.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public interface OpenInInventoryPlugin {
    List<OpenInInventoryPlugin> REGISTRY_EXPOSED_CUZ_LAZINESS = new ArrayList<>();

    default void registerAction(OpenActionRegistry registry) {
    }

    default void registerReplaceTemplate(Map<String, Collection<String>> registry) {
    }
}
