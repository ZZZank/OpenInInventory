package zank.mods.open_in_inventory.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenAction;

/// JSON representation:
/// ```json
/// {
///  "stack": ..., // see ItemStack.CODEC
///  "sneak": true // optional
/// }
/// ```
/// @author ZZZank
public record DefaultOpenAction(ItemStack stack, boolean sneak) implements OpenAction {
    public static final Codec<DefaultOpenAction> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(DefaultOpenAction::stack),
            Codec.BOOL.optionalFieldOf("sneak", OpenAction.SNEAK_DEFAULT).forGetter(DefaultOpenAction::sneak)
        ).apply(instance, DefaultOpenAction::new)
    );
}
