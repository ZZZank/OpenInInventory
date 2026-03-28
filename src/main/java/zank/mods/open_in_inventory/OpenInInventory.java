package zank.mods.open_in_inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.client.*;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.api.ScreenClosedEvent;
import zank.mods.open_in_inventory.impl.compat.*;
import zank.mods.open_in_inventory.impl.crt.ProvideCraftTweakerOpenAction;
import zank.mods.open_in_inventory.impl.handler.ClientCommandHandler;
import zank.mods.open_in_inventory.impl.handler.ClientEventHandler;
import zank.mods.open_in_inventory.impl.handler.ActionHandler;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;

import java.io.IOException;
import java.util.List;

/**
 * @author ZZZank
 */
public abstract class OpenInInventory {
    public static final String ID = "open_in_inventory";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static final Gson GSON = new GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create();

    public static OpenInInventory COMMON;
    public static final OpenActionRegistry ACTION_REGISTRY = new OpenActionRegistryImpl();
    public final ActionHandler actionHandler = new ActionHandler();

    public OpenInInventory() {
        registerPlugin(OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS);

        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientTooltipEvent.ITEM.register(actionHandler::tooltip);
            ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(actionHandler::beforeMouseClicked);
            ClientTickEvent.CLIENT_LEVEL_PRE.register(actionHandler::scheduleItemUse);
            ScreenClosedEvent.EVENT.register(actionHandler::screenClosed);
            ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientEventHandler::clientStarted);
            ClientCommandRegistrationEvent.EVENT.register(ClientCommandHandler::clientCommand);
        }
    }

    protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
        plugins.add(new CommonOpenInInventoryPlugin());
        plugins.add(new ProvideConfigOpenAction());
        if (Platform.isModLoaded("crafttweaker")) {
            plugins.add(new ProvideCraftTweakerOpenAction());
        }
    }

    public static boolean isScreenBlackListed(Screen screen) {
        if (screen == null) {
            return true; // I mean, yeah
        }
        var blacklist = OpenInInventoryConfig.SCREEN_BLACKLIST;
        return !blacklist.isEmpty() && blacklist.contains(screen.getClass().getName());
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

        var registry = (OpenActionRegistryImpl) OpenInInventory.ACTION_REGISTRY;

        // register template before using them (parse config)
        registry.replaceTemplates.clear();
        for (var plugin : OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS) {
            plugin.registerReplaceTemplate(registry.replaceTemplates);
        }
        for (var entry : registry.replaceTemplates.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue())); // trim
        }

        registry.internal.clear();
        for (var plugin : OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS) {
            plugin.registerAction(OpenInInventory.ACTION_REGISTRY);
        }
        for (var entry : registry.internal.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue())); // trim
        }
    }
}
