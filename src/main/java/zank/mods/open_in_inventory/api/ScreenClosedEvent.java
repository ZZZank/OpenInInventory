package zank.mods.open_in_inventory.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

import java.util.Objects;

/**
 * @author ZZZank
 */
public interface ScreenClosedEvent {
    // or EventFactory.createLoop()
    Event<Runnable> EVENT = EventFactory.of(list -> {
        var array = Objects.requireNonNull(list.toArray(new Runnable[0]));
        return () -> {
            for (var runnable : array) {
                runnable.run();
            }
        };
    });
}
