package zank.mods.open_in_inventory.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

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
        }
        if (replaceWith == null) {
            throw new IllegalArgumentException("Unknown template: " + template);
        }

        var list = new ArrayList<String>();
        for (var replaced : replaceWith) {
            list.addAll(findAndApplyTemplate(before + replaced + after));
        }
        return list;
    }
}
