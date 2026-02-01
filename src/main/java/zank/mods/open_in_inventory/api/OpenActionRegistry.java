package zank.mods.open_in_inventory.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collection;
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

    default OpenAction register(ItemStack stack) {
        return register(stack, false);
    }

    default OpenAction register(Item item, boolean sneak) {
        return register(item.getDefaultStack(), sneak);
    }

    default OpenAction register(Item stack) {
        return register(stack, false);
    }

    default Optional<OpenAction> registerIfPresent(Identifier itemId, boolean sneak) {
        var item = Registries.ITEM.get(itemId);
        return item == null ? Optional.empty() : Optional.of(register(item, sneak));
    }

    default Optional<OpenAction> registerIfPresent(Identifier itemId) {
        return registerIfPresent(itemId, false);
    }

    Collection<String> getReplaceTemplate(String key);
}
