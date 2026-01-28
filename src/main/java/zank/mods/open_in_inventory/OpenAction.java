package zank.mods.open_in_inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public record OpenAction(ItemStack stack, boolean sneakWhenUse) {
    private static final Map<Item, List<OpenAction>> REGISTRY = new HashMap<>();

    public static void register(ItemStack stack, boolean sneak) {
        REGISTRY.computeIfAbsent(stack.getItem(), k -> new ArrayList<>(3))
            .add(new OpenAction(stack, sneak));
    }

    @Nullable
    public static OpenAction get(ItemStack stack) {
        for (var action : REGISTRY.getOrDefault(stack.getItem(), List.of())) {
            if (ItemStack.areEqual(action.stack, stack)) {
                return action;
            }
        }
        return null;
    }
}
