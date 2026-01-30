

package zank.mods.open_in_inventory.neoforge;

import net.neoforged.fml.common.Mod;
import zank.mods.open_in_inventory.OpenInInventory;

@Mod(OpenInInventory.ID)
public class OpenInInventoryNeoForge extends OpenInInventory {
    public OpenInInventoryNeoForge() {
        COMMON = this;
    }
}
