package zank.mods.open_in_inventory.handler;

import net.minecraft.client.MinecraftClient;
import zank.mods.open_in_inventory.OpenInInventory;

/**
 * @author ZZZank
 */
public class ClientEventHandler {

    public static void clientStarted(MinecraftClient client) {
        OpenInInventory.refreshConfig();
    }
}
