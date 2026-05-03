package zank.mods.open_in_inventory.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zank.mods.open_in_inventory.api.ScreenClearedEvent;

/**
 * @author ZZZank
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void afterSetNewScreen(Screen screen, CallbackInfo ci) {
        if (this.screen == null) {
            ScreenClearedEvent.EVENT.invoker().onEvent((Minecraft) (Object) this);
        }
    }
}
