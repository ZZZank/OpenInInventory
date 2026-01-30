package zank.mods.open_in_inventory.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

import java.util.*;

/**
 * @author ZZZank
 */
public class OpenActionRegistryImpl implements OpenActionRegistry {
    public final Map<Item, List<OpenAction>> internal = new HashMap<>();

    @Override
    public Map<Item, List<OpenAction>> view() {
        return Collections.unmodifiableMap(internal);
    }

    @Override
    public OpenAction get(ItemStack stack) {
        for (var action : internal.getOrDefault(stack.getItem(), List.of())) {
            if (action.match(stack)) {
                return action;
            }
        }
        return null;
    }

    @Override
    public OpenAction register(ItemStack stack, boolean sneak) {
        var action = new OpenActionImpl(stack, sneak);
        internal.computeIfAbsent(stack.getItem(), k -> new ArrayList<>(3))
            .add(action);
        return action;
    }
}
