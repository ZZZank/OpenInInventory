package zank.mods.open_in_inventory.neoforge;

import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import zank.mods.open_in_inventory.impl.handler.ActionHandler;

import java.util.Objects;

/**
 * @author ZZZank
 */
public class ScreenClosedHandler {
    private final ActionHandler actionHandler;
    private boolean lastScreenPresent;

    public ScreenClosedHandler(ActionHandler actionHandler) {
        this.actionHandler = Objects.requireNonNull(actionHandler);
    }

    @SubscribeEvent
    public void tick(ClientTickEvent.Post event) {
        var client = MinecraftClient.getInstance();
        var currentScreenPresent = client.currentScreen != null;
        if (!currentScreenPresent && lastScreenPresent) {
            var result = actionHandler.onScreenChange(null);
            if (result.isPresent()) {
                client.setScreen(result.object());
            }
        }
        lastScreenPresent = currentScreenPresent;
    }
}
