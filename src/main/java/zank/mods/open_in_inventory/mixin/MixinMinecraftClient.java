package zank.mods.open_in_inventory.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zank.mods.open_in_inventory.api.ScreenClosedEvent;

/**
 * @author ZZZank
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void afterSetNewScreen(Screen screen, CallbackInfo ci) {
        if (screen == null) {
            ScreenClosedEvent.EVENT.invoker().run();
        }
    }
}
