package zank.mods.open_in_inventory.util;

import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public abstract class CommandUtil {

    public static Style clickToCopy(String value) {
        return Style.EMPTY
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy")));
    }

    public static void sendSuccess(
        ClientCommandRegistrationEvent.ClientCommandSourceStack source,
        Supplier<Text> message,
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
        Supplier<Text> message
    ) {
        sendSuccess(source, message, false);
    }

    public static void sendSuccess(
        CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> cx,
        Supplier<Text> message
    ) {
        sendSuccess(cx.getSource(), message);
    }
}
