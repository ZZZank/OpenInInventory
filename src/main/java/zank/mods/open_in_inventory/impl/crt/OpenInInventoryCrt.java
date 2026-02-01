package zank.mods.open_in_inventory.impl.crt;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
@ZenCodeType.Name("mods.open_in_inventory.OpenInInventory")
@ZenRegister
public abstract class OpenInInventoryCrt {
    @ZenCodeType.Field
    public static final LessGenericHandlerRegistry<OpenActionRegistry> ACTION_PROVIDERS
        = new LessGenericHandlerRegistry<>();

    @ZenCodeType.Field
    public static final LessGenericHandlerRegistry<Map<String, Collection<String>>> REPLACE_TEMPLATE_PROVIDERS
        = new LessGenericHandlerRegistry<>();

    /// Basically replacing `someMethod(T), T = Consumer<XXX>` with `someMethod(Consumer<T>), T = XXX`
    ///
    /// This is due to ZenCode missing generic handling
    @ZenCodeType.Name("mods.open_in_inventory.util.HandlerRegistry")
    public static class LessGenericHandlerRegistry<T> {
        private final List<Consumer<T>> handlers = new ArrayList<>();

        @ZenCodeType.Method
        public void register(Consumer<T> handler) {
            this.handlers.add(Objects.requireNonNull(handler));
        }

        @ZenCodeType.Method
        public void clear() {
            this.handlers.clear();
        }

        @ZenCodeType.Method
        public List<Consumer<T>> view() {
            return Collections.unmodifiableList(this.handlers);
        }
    }
}
