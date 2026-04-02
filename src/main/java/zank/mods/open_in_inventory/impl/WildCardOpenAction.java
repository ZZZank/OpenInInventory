package zank.mods.open_in_inventory.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenAction;

/**
 * @author ZZZank
 */
public record WildCardOpenAction(Item item) implements OpenAction {

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
