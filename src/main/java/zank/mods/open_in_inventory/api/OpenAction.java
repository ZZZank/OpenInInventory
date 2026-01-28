package zank.mods.open_in_inventory.api;

import net.minecraft.item.ItemStack;

/**
 * @author ZZZank
 */
public interface OpenAction {

    ItemStack stack();

    boolean sneak();

    default boolean match(ItemStack stack) {
        var match = this.stack();
        //? if <1.21 {
        if (match.hasNbt()) {
        //? } else
        //if (!match.getComponentChanges().isEmpty()) {
            return ItemStack.areEqual(match, stack);
        } else {
            return stack.isOf(match.getItem());
        }
    }
}
