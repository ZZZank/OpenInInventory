package zank.mods.open_in_inventory.impl.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;
import zank.mods.open_in_inventory.util.CommandUtil;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.argument;
import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;

/**
 * @author ZZZank
 */
public class ClientCommand {
    public static void register(
        CommandDispatcher<ClientCommandSourceStack> dispatcher,
        CommandBuildContext context
    ) {
        // open-in-inventory is easier to typed
        dispatcher.register(literal(OpenInInventory.ID.replace('_', '-'))
            .then(literal("refresh")
                .executes(ClientCommand::refresh)
            )
            .then(literal("hand")
                .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.SHOW)))
                .then(literal("--wildcard")
                    .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.SHOW, CommandAdd.WILDCARD))))
            )
            .then(literal("hotbar")
                .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.HOTBAR, CommandAdd.SHOW)))
                .then(literal("--wildcard")
                    .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.HOTBAR, CommandAdd.SHOW, CommandAdd.WILDCARD))))
            )
            .then(literal("replaceTemplate")
                .then(argument("key", StringArgumentType.string())
                    .suggests(ClientCommand::suggestReplaceTemplate)
                    .executes(ClientCommand::replaceTemplate)
                )
            )
            .then(literal("add")
                .then(argument("args", StringArgumentType.greedyString())
                    .suggests(CommandAdd::suggest)
                    .executes(CommandAdd::execute))
            )
        );
    }

    private static int refresh(CommandContext<ClientCommandSourceStack> cx) {
        OpenInInventory.refreshConfig();
        OpenInInventory.COMMON.actionHandler.reset();
        CommandUtil.sendSuccess(cx, () -> Component.translatable("open_in_inventory.command.refresh"));
        return Command.SINGLE_SUCCESS;
    }

    private static int replaceTemplate(CommandContext<ClientCommandSourceStack> cx) {
        var key = cx.getArgument("key", String.class);
        var replace = OpenInInventory.ACTION_REGISTRY.getReplaceTemplate(key);
        Supplier<Component> message = () -> Component.empty()
            .append(Component.literal(key).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)))
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
        return SharedSuggestionProvider.suggest(registry.replaceTemplates.keySet(), builder);
    }
}
