package zank.mods.open_in_inventory.impl.crt;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
@ZenCodeType.Name("mods.open_in_inventory.OpenInInventory")
@ZenRegister
public abstract class OpenInInventoryCrt {
    static final List<Consumer<OpenActionRegistry>> PROVIDERS = new ArrayList<>();

    public static void registerActionProvider(Consumer<OpenActionRegistry> provider) {
        PROVIDERS.add(Objects.requireNonNull(provider));
    }

    public static void clearActionProviders() {
        PROVIDERS.clear();
    }

    public static List<Consumer<OpenActionRegistry>> viewRegisteredProviders() {
        return Collections.unmodifiableList(PROVIDERS);
    }
}
