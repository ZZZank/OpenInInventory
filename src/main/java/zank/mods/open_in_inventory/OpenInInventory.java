package zank.mods.open_in_inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.client.*;
import dev.architectury.platform.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zank.mods.open_in_inventory.handler.ClientEventHandler;
import zank.mods.open_in_inventory.handler.ActionHandler;

import java.io.IOException;

/**
 * @author ZZZank
 */
public class OpenInInventory {
    public static final String ID = "open_in_inventory";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static final Gson GSON = new GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create();

    public final ActionHandler actionHandler = new ActionHandler();

    public OpenInInventory() {
        ClientTooltipEvent.ITEM.register(actionHandler::tooltip);
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(actionHandler::beforeMouseClicked);
        ClientTickEvent.CLIENT_LEVEL_PRE.register(actionHandler::scheduleItemUse);
        ClientGuiEvent.SET_SCREEN.register(actionHandler::onScreenChange);
        ClientLifecycleEvent.CLIENT_STARTED.register(ClientEventHandler::clientStarted);
        ClientCommandRegistrationEvent.EVENT.register(ClientEventHandler::clientCommand);
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

    public static void refreshConfig() {
        try {
            OpenInInventoryConfig.refresh(Platform.getConfigFolder().resolve(OpenInInventory.ID + ".json"));
        } catch (IOException e) {
            OpenInInventory.LOGGER.error("Error when refreshing config", e);
        }

        OpenAction.REGISTRY.clear();
        for (var intermediate : OpenInInventoryConfig.ENABLED_ITEMS) {
            try {
                var action = intermediate.toAction();
                OpenAction.register(action.stack(), action.sneakWhenUse());
            } catch (Exception e) {
                LOGGER.error("Error when parsing `enabledItems` from config", e);
            }
        }
    }
}
