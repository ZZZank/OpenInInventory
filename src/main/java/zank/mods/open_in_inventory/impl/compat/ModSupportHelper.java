package zank.mods.open_in_inventory.impl.compat;

import dev.architectury.platform.Platform;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;

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

    public ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(mod, path);
    }

    /// find and apply all possible templates in `path`, and try to register all of them. Failed registration will cause
    /// an error logging
    ///
    /// @return Successfully registered [OpenAction]
    public Collection<OpenAction> tryRegister(String path, boolean sneak) {
        var registered = new ArrayList<OpenAction>();
        for (var applied : registry.findAndApplyTemplate(path)) {
            var result = registry.registerIfPresent(id(applied), sneak);
            if (result.isEmpty()) {
                OpenInInventory.LOGGER.error("Cannot find item ith id: {}", id(path));
            } else {
                registered.add(result.get());
            }
        }
        return registered;
    }

    /// @see #tryRegister(String, boolean)
    public Collection<OpenAction> tryRegister(String path) {
        return tryRegister(path, false);
    }
}
