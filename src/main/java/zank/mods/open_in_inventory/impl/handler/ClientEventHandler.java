package zank.mods.open_in_inventory.impl.handler;

import net.minecraft.client.world.ClientWorld;
import zank.mods.open_in_inventory.OpenInInventory;

/**
 * @author ZZZank
 */
public class ClientEventHandler {

    public static void clientStarted(ClientWorld world) {
        OpenInInventory.refreshConfig();
    }

}
