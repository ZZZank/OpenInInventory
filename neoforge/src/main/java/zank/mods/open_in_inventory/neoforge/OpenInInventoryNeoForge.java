

package zank.mods.open_in_inventory.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import zank.mods.open_in_inventory.OpenInInventory;

@Mod(OpenInInventory.ID)
public class OpenInInventoryNeoForge extends OpenInInventory {
    public OpenInInventoryNeoForge() {
        COMMON = this;
        NeoForge.EVENT_BUS.register(new ScreenClosedHandler(actionHandler));
    }
}
