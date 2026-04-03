package zank.mods.open_in_inventory.impl.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;
import zank.mods.open_in_inventory.util.CommandUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.argument;
import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;

/**
 * @author ZZZank
 */
public class ClientCommandHandler {
    public static void clientCommand(
        CommandDispatcher<ClientCommandSourceStack> dispatcher,
        CommandRegistryAccess context
    ) {
        // open-in-inventory is easier to typed
        dispatcher.register(literal(OpenInInventory.ID.replace('_', '-'))
            .then(literal("refresh")
                .executes(ClientCommandHandler::refresh)
            )
            .then(literal("hand")
                .executes(cx -> hand(cx, false))
                .then(literal("--id").executes(cx -> hand(cx, true)))
            )
            .then(literal("hotbar")
                .executes(ClientCommandHandler::hotbar))
            .then(literal("replaceTemplate")
                .then(argument("key", StringArgumentType.string())
                    .suggests(ClientCommandHandler::suggestReplaceTemplate)
                    .executes(ClientCommandHandler::replaceTemplate)
                )
            )
        );
    }

    private static int hotbar(CommandContext<ClientCommandSourceStack> context) {
        context.getSource().arch$getPlayer().getInventory().getSwappableHotbarSlot();
        return 0;
    }

    private static int refresh(CommandContext<ClientCommandSourceStack> cx) {
        OpenInInventory.refreshConfig();
        OpenInInventory.COMMON.actionHandler.reset();
        CommandUtil.sendSuccess(cx.getSource(), () -> Text.translatable("open_in_inventory.command.refresh.done"));
        return Command.SINGLE_SUCCESS;
    }

    private static int hand(CommandContext<ClientCommandSourceStack> cx, boolean idOnly) {
        var stack = cx.getSource().arch$getPlayer().getMainHandStack();

        String msg;
        if (idOnly) {
            msg = OpenInInventory.GSON.toJson(stack.getItem().toString());
        } else {
            msg = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack)
                .resultOrPartial(error -> cx.getSource().arch$sendFailure(Text.literal(error)))
                .map(OpenInInventory.GSON::toJson)
                .orElse(null);
            if (msg == null) {
                return 0;
            }
        }
        CommandUtil.sendSuccess(cx.getSource(), () -> Text.literal(msg).fillStyle(CommandUtil.clickToCopy(msg)));
        return Command.SINGLE_SUCCESS;
    }

    private static int replaceTemplate(CommandContext<ClientCommandSourceStack> cx) {
        var key = cx.getArgument("key", String.class);
        var replace = OpenInInventory.ACTION_REGISTRY.getReplaceTemplate(key);
        Supplier<Text> message = () -> Text.empty()
            .append(Text.literal(key).setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
            .append(" -> ")
            .append(OpenInInventory.GSON.toJson(replace));
        CommandUtil.sendSuccess(cx, message);
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestReplaceTemplate(
        CommandContext<ClientCommandSourceStack> cx,
        SuggestionsBuilder builder
    ) {
        var registry = (OpenActionRegistryImpl) OpenInInventory.ACTION_REGISTRY;
        return CommandSource.suggestMatching(registry.replaceTemplates.keySet(), builder);
    }
}
