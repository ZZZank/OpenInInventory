package zank.mods.open_in_inventory.api;

import net.minecraft.world.item.ItemStack;

/**
 * @author ZZZank
 */
public interface OpenAction {
    boolean SNEAK_DEFAULT = false;

    ItemStack stack();

    boolean sneak();

    default boolean match(ItemStack stack) {
        var match = this.stack();
        //? if <1.21 {
        if (match.hasTag()) {
        //? } else
        //if (!match.getComponentChanges().isEmpty()) {
            return ItemStack.matches(match, stack);
        } else {
            return stack.is(match.getItem());
        }
    }
}
