package zank.mods.open_in_inventory.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//? if < 1.20 {
/*import net.minecraft.util.registry.Registry;
*///? } else
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ZZZank
 */
public interface OpenActionRegistry {

    Map<Item, List<OpenAction>> view();

    OpenAction get(ItemStack stack);

    OpenAction register(ItemStack stack, boolean sneak);

    default OpenAction register(Item item, boolean sneak) {
        return register(item.getDefaultStack(), sneak);
    }

    default Optional<OpenAction> registerIfPresent(Identifier itemId, boolean sneak) {
        var item = /*? if <1.20 {*//*Registry*//*?} else {*/Registries/*?}*/.ITEM.get(itemId);
        return item == null ? Optional.empty() : Optional.of(register(item, sneak));
    }
}
