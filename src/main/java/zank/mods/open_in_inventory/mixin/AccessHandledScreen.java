package zank.mods.open_in_inventory.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(AbstractContainerScreen.class)
public interface AccessHandledScreen {

    @Accessor
    Slot getHoveredSlot();
}
