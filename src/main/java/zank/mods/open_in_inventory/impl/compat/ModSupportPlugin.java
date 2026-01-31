package zank.mods.open_in_inventory.impl.compat;

import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

/**
 * @author ZZZank
 */
public interface ModSupportPlugin extends OpenInInventoryPlugin {

    String requiredModId();

    default Identifier id(String path) {
        return Identifier.of(requiredModId(), path);
    }
}
