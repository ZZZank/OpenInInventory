package zank.mods.open_in_inventory;

import dev.architectury.event.events.client.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ZZZank
 */
public class OpenInInventory {
    public static final String ID = "open_in_inventory";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public OpenInInventory() {
        ClientTooltipEvent.ITEM.register(ClientEventHandler::tooltip);
        /// less performance heavy than [ClientRawInputEvent], but not much different
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(ClientEventHandler::beforeMouseClicked);
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ClientEventHandler::scheduleItemUse);
        ClientGuiEvent.SET_SCREEN.register(ClientEventHandler::onScreenChange);
    }

    public static boolean isScreenBlackListed(Screen screen) {
        if (screen == null) {
            return true; // I mean, yeah
        } else if (screen instanceof CreativeInventoryScreen creative && creative.isInventoryTabSelected()) {
            // for some reason, creative inventory will eat your item
            return true;
        }
        return OpenInInventoryConfig.SCREEN_BLACKLIST.contains(screen.getClass().getName());
    }

    public static boolean isShiftPressed(MinecraftClient client) {
        var handle = client.getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_SHIFT)
               || InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_RIGHT_SHIFT);
    }
}
