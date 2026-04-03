package zank.mods.open_in_inventory.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

import java.util.Objects;

/// After [net.minecraft.client.Minecraft#screen] became `null`
///
/// @author ZZZank
public interface ScreenClosedEvent {
    Event<Runnable> EVENT = EventFactory.of(list -> {
        var array = Objects.requireNonNull(list.toArray(new Runnable[0]));
        return () -> {
            for (var runnable : array) {
                runnable.run();
            }
        };
    });
}
