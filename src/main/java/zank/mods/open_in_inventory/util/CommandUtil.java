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
        //? if <26.1
        var click = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value);
        //? else
        //var click = new ClickEvent.CopyToClipboard(value);

        return hover(Component.literal("Click to copy")).withClickEvent(click);
    }

    public static Style hover(Component value) {
        //? if <26.1
        var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, value);
        //? else
        //var hover = new HoverEvent.ShowText(value);

        return Style.EMPTY.withHoverEvent(hover);
    }

    public static void sendSuccess(
        ClientCommandRegistrationEvent.ClientCommandSourceStack source,
        Supplier<Component> message,
        boolean notifyAdmin
    ) {
        //? if < 1.20 {
        /*source.arch$sendSuccess(message.get(), notifyAdmin);
        *///? } else {
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
