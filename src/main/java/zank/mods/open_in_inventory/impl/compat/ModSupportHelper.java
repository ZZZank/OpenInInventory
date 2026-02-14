package zank.mods.open_in_inventory.impl.compat;

import dev.architectury.platform.Platform;
import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.Optional;

/**
 * @author ZZZank
 */
public final class ModSupportHelper {
    private final OpenActionRegistry registry;
    private String mod;

    public ModSupportHelper(OpenActionRegistry registry) {
        this.registry = registry;
    }

    public boolean check(String mod) {
        this.mod = mod;
        return Platform.isModLoaded(mod);
    }

    public Identifier id(String path) {
        return Identifier.of(mod, path);
    }

    public Optional<OpenAction> tryRegister(String path, boolean sneak) {
        var result = registry.registerIfPresent(id(path), sneak);
        if (result.isEmpty()) {
            OpenInInventory.LOGGER.error("Cannot find item ith id: {}", id(path));
        }
        return result;
    }

    public Optional<OpenAction> tryRegister(String path) {
        return tryRegister(path, false);
    }
}
