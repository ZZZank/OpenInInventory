package zank.mods.open_in_inventory.impl;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import zank.mods.open_in_inventory.api.OpenAction;

/// JSON representation:
/// ```json
/// "minecraft:apple"
/// ```
/// @author ZZZank
public record WildCardOpenAction(Item item) implements OpenAction {
    public static final Codec<WildCardOpenAction> CODEC = Registries.ITEM
        .getCodec()
        .xmap(WildCardOpenAction::new, WildCardOpenAction::item);

    @Override
    public ItemStack stack() {
        return item.getDefaultStack();
    }

    @Override
    public boolean sneak() {
        return OpenAction.SNEAK_DEFAULT;
    }

    @Override
    public boolean match(ItemStack stack) {
        return stack.isOf(item);
    }
}
