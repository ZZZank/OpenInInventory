package zank.mods.open_in_inventory.api;


import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.Minecraft;

/// After [net.minecraft.client.Minecraft#screen] became `null`
///
/// @author ZZZank
public interface ScreenClearedEvent {
    Event<ScreenClearedEvent> EVENT = EventFactory.createLoop();

    void onEvent(Minecraft client);
}
