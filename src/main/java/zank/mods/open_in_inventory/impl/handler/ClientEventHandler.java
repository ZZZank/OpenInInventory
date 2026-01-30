package zank.mods.open_in_inventory.impl.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import zank.mods.open_in_inventory.OpenInInventory;

import java.util.function.Supplier;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.*;

/**
 * @author ZZZank
 */
public class ClientEventHandler {

    public static void clientStarted(ClientWorld world) {
        OpenInInventory.refreshConfig();
    }

    public static void clientCommand(
        CommandDispatcher<ClientCommandSourceStack> dispatcher,
        CommandRegistryAccess context
    ) {
        // open-in-inventory is easier to typed
        dispatcher.register(literal(OpenInInventory.ID.replace('_', '-'))
            .then(literal("refresh")
                .executes(cx -> {
                    OpenInInventory.refreshConfig();
                    OpenInInventory.COMMON.actionHandler.reset();
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(literal("hand")
                .executes(cx -> {
                    var stack = cx.getSource().arch$getPlayer().getMainHandStack();
                    ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack)
                        .resultOrPartial(error -> cx.getSource().arch$sendFailure(Text.literal(error)))
                        .map(OpenInInventory.GSON::toJson)
                        .ifPresent(msg -> {
                            Supplier<Text> message = () -> Text.literal(msg)
                                .fillStyle(Style.EMPTY
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, msg))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy"))));
                            cx.getSource().arch$sendSuccess(message/*?if <1.20 {*//*.get()*//*?}*/, false);
                        });
                    return Command.SINGLE_SUCCESS;
                }))
        );
    }
}
