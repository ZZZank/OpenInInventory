package zank.mods.open_in_inventory.impl.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import zank.mods.open_in_inventory.OpenInInventory;

/**
 * @author ZZZank
 */
public class ClientEventHandler {

    public static void clientStarted(MinecraftClient client) {
        OpenInInventory.refreshConfig();
    }

    public static void clientCommand(
        CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher,
        CommandRegistryAccess context
    ) {
        dispatcher.register(
            ClientCommandRegistrationEvent.literal(OpenInInventory.ID)
                .then(
                    ClientCommandRegistrationEvent.literal("refresh")
                        .executes(cx -> {
                            OpenInInventory.refreshConfig();
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
