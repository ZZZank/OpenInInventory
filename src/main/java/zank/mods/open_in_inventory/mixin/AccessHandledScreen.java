package zank.mods.open_in_inventory.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(HandledScreen.class)
public interface AccessHandledScreen {

    @Accessor
    Slot getFocusedSlot();
}
