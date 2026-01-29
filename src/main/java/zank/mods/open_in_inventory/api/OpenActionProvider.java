package zank.mods.open_in_inventory.api;

import dev.architectury.platform.Platform;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.ServiceLoader;

/**
 * @author ZZZank
 */
public interface OpenActionProvider {
    List<OpenActionProvider> SERVICES = ServiceLoader.load(OpenActionProvider.class)
        .stream()
        .map(ServiceLoader.Provider::get)
        .toList();

    default boolean enabled() {
        return true;
    }

    void register(OpenActionRegistry registry);

    interface RequireMod extends OpenActionProvider {

        String requiredModId();

        @Override
        default boolean enabled() {
            return Platform.isModLoaded(requiredModId());
        }

        default Identifier id(String path) {
            return Identifier.of(requiredModId(), path);
        }
    }
}
