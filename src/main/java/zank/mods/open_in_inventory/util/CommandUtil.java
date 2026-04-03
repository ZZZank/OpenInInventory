package zank.mods.open_in_inventory.util;

import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import java.util.function.Supplier;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

/**
 * @author ZZZank
 */
public abstract class CommandUtil {

    public static Style clickToCopy(String value) {
        return Style.EMPTY
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")));
    }

    public static Style hover(Component value) {
        return Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, value));
    }

    public static void sendSuccess(
        ClientCommandRegistrationEvent.ClientCommandSourceStack source,
        Supplier<Component> message,
        boolean notifyAdmin
    ) {
        //? if < 1.20 {
        //source.arch$sendSuccess(message.get(), notifyAdmin);
        //? } else {
        source.arch$sendSuccess(message, notifyAdmin);
        //? }
    }

    public static void sendSuccess(
        ClientCommandRegistrationEvent.ClientCommandSourceStack source,
        Supplier<Component> message
    ) {
        sendSuccess(source, message, false);
    }

    public static void sendSuccess(
        CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> cx,
        Supplier<Component> message
    ) {
        sendSuccess(cx.getSource(), message, false);
    }
}
