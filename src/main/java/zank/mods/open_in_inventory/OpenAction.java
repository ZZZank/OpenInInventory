package zank.mods.open_in_inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public record OpenAction(ItemStack stack, boolean sneakWhenUse) {
    public static final Map<Item, List<OpenAction>> REGISTRY = new HashMap<>();

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

    public Intermediate toIntermediate() {
        return new Intermediate(
            Registries.ITEM.getId(stack.getItem()).toString(),
            stack.getCount(),
            stack.getNbt(),
            sneakWhenUse
        );
    }

    public static class Intermediate {
        private static final int COUNT_DEFAULT = 1;
        private static final boolean SNEAK_DEFAULT = false;

        public String id;
        public Integer count;
        public NbtCompound tag;
        public Boolean sneak;

        public Intermediate(String id, int count, NbtCompound tag, boolean sneak) {
            this.id = id;
            this.count = count != COUNT_DEFAULT ? count : null;
            this.tag = tag;
            this.sneak = sneak != SNEAK_DEFAULT ? sneak : null;
        }

        public OpenAction toAction() {
            var stack = Registries.ITEM.get(Identifier.tryParse(id)).getDefaultStack().copy();
            stack.setCount(count == null ? COUNT_DEFAULT : count);
            stack.setNbt(tag);
            return new OpenAction(stack, sneak == null ? SNEAK_DEFAULT : sneak);
        }
    }
}
