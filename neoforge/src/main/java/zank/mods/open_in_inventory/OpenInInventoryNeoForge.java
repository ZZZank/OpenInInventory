

package zank.mods.open_in_inventory;

import net.neoforged.fml.common.Mod;

@Mod(OpenInInventory.ID)
public class OpenInInventoryNeoForge {
    public static OpenInInventory COMMON;

    public OpenInInventoryNeoForge() {
        COMMON = new OpenInInventory();
    }
}
