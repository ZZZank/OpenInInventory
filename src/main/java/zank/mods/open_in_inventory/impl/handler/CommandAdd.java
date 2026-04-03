package zank.mods.open_in_inventory.impl.handler;

import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.impl.DefaultOpenAction;
import zank.mods.open_in_inventory.impl.WildCardOpenAction;
import zank.mods.open_in_inventory.util.CommandOptions;
import zank.mods.open_in_inventory.util.CommandOptions.CommandOption;
import zank.mods.open_in_inventory.util.CommandUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author ZZZank
 */
abstract class CommandAdd {
    public static final CommandOption HOTBAR = new CommandOption("hotbar");
    public static final CommandOption WILDCARD = new CommandOption("wildcard", "w");
    public static final CommandOption SNEAK = new CommandOption("sneak", "s");
    public static final CommandOption SHOW = new CommandOption("show");

    public static final CommandOptions OPTIONS = new CommandOptions(HOTBAR, WILDCARD, SNEAK, SHOW);

    static CompletableFuture<Suggestions> suggest(
        CommandContext<ClientCommandSourceStack> cx,
        SuggestionsBuilder builder
    ) {
        var remaining = builder.getRemaining();

        var lastSpaceAt = remaining.lastIndexOf(' ');
        if (lastSpaceAt >= 0) {
            builder = builder.createOffset(builder.getStart() + lastSpaceAt + 1);
        }

        var suggested = OPTIONS.suggestNext(remaining);
        for (var option : suggested) {
            builder.suggest("--" + option.name(), Text.translatable("open_in_inventory.command.add.option." + option.name()));
            if (option.hasShorthand()) {
                builder.suggest("-" + option.shorthand(), Text.literal("Equivalent of: ").append("--" + option.name()));
            }
        }
        return builder.buildFuture();
    }

    public static int execute(CommandContext<ClientCommandSourceStack> cx) {
        var player = cx.getSource().arch$getPlayer();
        var options = OPTIONS.parse(cx.getArgument("args", String.class));

        var stacks = options.contains(HOTBAR)
            ? player.getInventory().main.subList(0, 9)
            : List.of(player.getMainHandStack());

        Function<ItemStack, OpenAction> actionCtor;
        if (options.contains(WILDCARD)) {
            if (options.contains(SNEAK)) {
                actionCtor = stack -> new DefaultOpenAction(new ItemStack(stack.getItem()), true);
            } else {
                actionCtor = stack -> new WildCardOpenAction(stack.getItem());
            }
        } else {
            var shift = options.contains(SNEAK);
            actionCtor = stack -> new DefaultOpenAction(stack, shift);
        }

        var actionJsons = stacks
            .stream()
            .filter(stack -> !stack.isEmpty())
            .map(actionCtor)
            .map(action -> {
                if (action instanceof DefaultOpenAction def) {
                    return DefaultOpenAction.CODEC.encodeStart(JsonOps.INSTANCE, def);
                } else if (action instanceof WildCardOpenAction wild) {
                    return WildCardOpenAction.CODEC.encodeStart(JsonOps.INSTANCE, wild);
                }
                throw new IllegalArgumentException("Unknown OpenAction instance: " + action);
            })
            .flatMap(result -> result.resultOrPartial(OpenInInventory.LOGGER::error).stream())
            .toList();

        if (actionJsons.isEmpty()) {
            CommandUtil.sendSuccess(cx, () -> Text.literal("No items to add, skipping"));
            return 0;
        }

        if (options.contains(SHOW)) {
            CommandUtil.sendSuccess(
                cx,
                () -> Text.translatable(
                    "open_in_inventory.command.add.show",
                    Text.literal(String.valueOf(actionJsons.size())).formatted(Formatting.GRAY),
                    Text.literal(OpenInInventory.GSON.toJson(actionJsons))
                        .fillStyle(CommandUtil.clickToCopy(OpenInInventory.GSON.toJson(actionJsons)))
                        .formatted(Formatting.GREEN)
                )
            );
            return 1;
        }

        try {
            addToCfg(cx, actionJsons);
            return Command.SINGLE_SUCCESS;
        } catch (IOException e) {
            cx.getSource().arch$sendFailure(Text.literal("Failed to save config: " + e));
            return 0;
        }
    }

    private static void addToCfg(CommandContext<ClientCommandSourceStack> cx, List<JsonElement> actionJsons) throws IOException {
        var config = OpenInInventory.CONFIG;
        for (var actionJson : actionJsons) {
            config.enabledItems().add(actionJson);
        }

        config.write(OpenInInventory.CONFIG_PATH);

        OpenInInventory.refreshConfig();

        CommandUtil.sendSuccess(
            cx.getSource(), () -> Text.translatable(
                "open_in_inventory.command.add",
                Text.literal(String.valueOf(actionJsons.size())).formatted(Formatting.GRAY)
            ).fillStyle(CommandUtil.hover(Text.literal(OpenInInventory.GSON.toJson(actionJsons))))
        );
    }
}
