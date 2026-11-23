package zank.mods.open_in_inventory.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author ZZZank
 */
@Mixin(AbstractContainerScreen.class)
public interface AccessHandledScreen {

    @Accessor
    Slot getHoveredSlot();

    @Invoker
    void invokeSlotClicked(
        Slot slot,
        int slotId,
        int buttonNum,
        //$ if <26.1 'net.minecraft.world.inventory.ClickType input' else 'net.minecraft.world.inventory.ContainerInput input'
        net.minecraft.world.inventory.ClickType input
    );
}
