package zank.mods.open_in_inventory.api;

import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author ZZZank
 */
public interface OpenActionRegistry {

    Map<Item, List<OpenAction>> view();

    OpenAction get(ItemStack stack);

    OpenAction register(ItemStack stack, boolean sneak);

    /// Equivalent of `register(..., false)`
    /// @see #register(ItemStack, boolean)
    default OpenAction register(ItemStack stack) {
        return register(stack, false);
    }

    default OpenAction register(Item item, boolean sneak) {
        return register(item.getDefaultInstance(), sneak);
    }

    /// Equivalent of `register(..., false)`
    /// @see #register(Item, boolean)
    default OpenAction register(Item item) {
        return register(item, false);
    }

    default Optional<OpenAction> registerIfPresent(ResourceLocation itemId, boolean sneak) {
        var item = BuiltInRegistries.ITEM.get(itemId);
        return item == null ? Optional.empty() : Optional.of(register(item, sneak));
    }

    /// Equivalent of `registerIfPresent(..., false)`
    /// @see #registerIfPresent(Identifier, boolean)
    default Optional<OpenAction> registerIfPresent(ResourceLocation itemId) {
        return registerIfPresent(itemId, false);
    }

    /// search for registered template with such key, return `null` if not found
    ///
    /// immediate template is not supported, use [#findAndApplyTemplate(String)] instead
    Collection<String> getReplaceTemplate(String key);

    /// Supports:
    /// - registered template: {color}
    /// - immediate template: {iron|gold|diamond}
    ///
    /// @see OpenInInventoryPlugin#registerReplaceTemplate(Map)
    default Collection<String> findAndApplyTemplate(String original) {
        // example: some_mod:{color}_bag

        var left = original.indexOf('{');
        if (left < 0) {
            return List.of(original);
        }

        var right = original.indexOf('}', left);
        if (right < 0) {
            throw new IllegalArgumentException("Found '{', but no matching '}' in string: " + original);
        }

        var before = original.substring(0, left); // some_mod:
        var after = original.substring(right + 1); // _bag
        var template = original.substring(left + 1, right); // color

        // template key {color}, or immediate template {iron|gold|diamond}
        Collection<String> replaceWith;
        if (template.indexOf('|') >= 0) {
            replaceWith = Arrays.asList(template.split("\\|"));
        } else {
            replaceWith = getReplaceTemplate(template);
            if (replaceWith == null) {
                throw new IllegalArgumentException("Unknown template: " + template);
            }
        }

        var list = new ArrayList<String>();
        for (var replaced : replaceWith) {
            list.addAll(findAndApplyTemplate(before + replaced + after));
        }
        return list;
    }
}
