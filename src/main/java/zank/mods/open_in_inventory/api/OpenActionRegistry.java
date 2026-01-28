package zank.mods.open_in_inventory.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public interface OpenActionRegistry {

    Map<Item, List<OpenAction>> view();

    OpenAction get(ItemStack stack);

    OpenAction register(ItemStack stack, boolean sneak);

    default void register(Item item, boolean sneak) {
        register(item.getDefaultStack(), sneak);
    }
}
