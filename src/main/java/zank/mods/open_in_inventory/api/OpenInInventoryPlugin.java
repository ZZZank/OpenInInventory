package zank.mods.open_in_inventory.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZZZank
 */
public interface OpenInInventoryPlugin {
    List<OpenInInventoryPlugin> REGISTRY_EXPOSED_CUZ_LAZINESS = new ArrayList<>();

    default void registerAction(OpenActionRegistry registry) {
    }
}
