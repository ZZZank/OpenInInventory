package zank.mods.open_in_inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.*;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.api.ScreenClosedEvent;
import zank.mods.open_in_inventory.impl.compat.*;
import zank.mods.open_in_inventory.impl.crt.ProvideCraftTweakerOpenAction;
import zank.mods.open_in_inventory.impl.handler.ClientCommand;
import zank.mods.open_in_inventory.impl.handler.ClientEventHandler;
import zank.mods.open_in_inventory.impl.handler.ActionHandler;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

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
    public static OpenInInventoryConfig CONFIG = new OpenInInventoryConfig();
    public static Path CONFIG_PATH;
    public static final OpenActionRegistry ACTION_REGISTRY = new OpenActionRegistryImpl();

    public final ActionHandler actionHandler = new ActionHandler();

    public OpenInInventory() {
        registerPlugin(OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS);

        CONFIG_PATH = Platform.getConfigFolder().resolve(ID + ".json");

        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientTooltipEvent.ITEM.register(actionHandler::tooltip);
            ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(actionHandler::beforeMouseClicked);
            ClientTickEvent.CLIENT_LEVEL_PRE.register(actionHandler::scheduleItemUse);
            ScreenClosedEvent.EVENT.register(actionHandler::screenClosed);
            ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientEventHandler::clientStarted);
            ClientCommandRegistrationEvent.EVENT.register(ClientCommand::register);
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
        var blacklist = OpenInInventory.CONFIG.screenBlacklist();
        return !blacklist.isEmpty() && blacklist.contains(screen.getClass().getName());
    }

    public static boolean isShiftPressed(Minecraft client) {
        var handle = client.getWindow().getWindow();
        return InputConstants.isKeyDown(handle, InputConstants.KEY_LSHIFT)
               || InputConstants.isKeyDown(handle, InputConstants.KEY_RSHIFT);
    }

    public static void refreshConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
                CONFIG = OpenInInventory.GSON.fromJson(reader, OpenInInventoryConfig.class);
            } catch (IOException e) {
                LOGGER.error("Error when reading config", e);
            }
        }
        try {
            CONFIG.write(CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.error("Error when writing config", e);
        }

        var registry = (OpenActionRegistryImpl) ACTION_REGISTRY;

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
