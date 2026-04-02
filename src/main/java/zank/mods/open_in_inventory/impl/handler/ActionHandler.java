package zank.mods.open_in_inventory.impl.handler;

import dev.architectury.event.EventResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
//? if <1.21 {
import net.minecraft.client.item.TooltipContext;
//? } else
//import net.minecraft.item.Item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.mixin.AccessHandledScreen;

import java.util.List;

/**
 * @author ZZZank
 */
public class ActionHandler {
    /// we assume that both [#swapFrom] and [#swapTo] is targeting [InventoryScreen]
    private int swapFrom;
    /// we assume that both [#swapFrom] and [#swapTo] is targeting [InventoryScreen]
    ///
    /// Always point to a slot in hotbar, so will always be less than 9
    private int swapTo;

    private ActionStage stage;
    private long itemUseAtTime;
    private OpenAction action;
    private boolean shouldUpdateSneak;

    public ActionHandler() {
        reset();
    }

    public void reset() {
        stage = ActionStage.IDLE;
        itemUseAtTime = -1;
        action = null;
        shouldUpdateSneak = false;
    }

    public EventResult beforeMouseClicked(
        MinecraftClient client,
        Screen _screen,
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return EventResult.pass();
        }
        var player = client.player;
        var world = client.world;
        if (player != null && world != null && _screen instanceof HandledScreen<?> screen) {
            var focused = ((AccessHandledScreen) screen).getFocusedSlot();
            var matched = matchAction(_screen, player, focused);
            if (matched == null) {
                return EventResult.pass();
            }

            swapFrom = focused.getIndex();
            swapTo = player.getInventory().selectedSlot;

            var stackBeforeSwap = focused.getStack();

            if (OpenInInventory.CONFIG.debug()) {
                OpenInInventory.LOGGER.info(
                    "Attempt to swap slot(index {}, id {}) with hotbar {} in gui {}",
                    focused.getIndex(),
                    focused.id,
                    swapTo,
                    screen
                );
            }

            if (swapFrom < 9) {
                // In Forge, swapping stack between stacks in hotbar will fail
                // I don't know why, but let's just avoid swapping stack
                player.getInventory().selectedSlot = swapFrom;
            } else {
                /// the screen is not always [InventoryScreen], so we sometimes use [Slot#id]
                /// (relative to [ScreenHandler]) instead of [Slot#getIndex()]
                var actualSwapFrom = screen instanceof AbstractInventoryScreen
                    ? focused.getIndex()
                    : focused.id;
                performSwap(client, screen, actualSwapFrom, player);
            }

            if (player.getMainHandStack() != stackBeforeSwap) {
                return EventResult.pass();
            }

            shouldUpdateSneak = matched.sneak() != client.options.sneakKey.isPressed();
            if (shouldUpdateSneak) {
                if (client.options.getSneakToggled().getValue()) {
                    client.options.sneakKey.setPressed(true);
                } else {
                    client.options.sneakKey.setPressed(matched.sneak());
                }
            }

            itemUseAtTime = world.getTime() + OpenInInventory.CONFIG.openDelay();
            stage = ActionStage.SWAPPED;
            action = matched;

            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }

    private void performSwap(MinecraftClient client, HandledScreen<?> screen, int swapFrom, ClientPlayerEntity player) {
        assert client.interactionManager != null;
        client.interactionManager.clickSlot(
            screen.getScreenHandler().syncId,
            swapFrom,
            this.swapTo,
            SlotActionType.SWAP,
            player
        );
    }

    public void scheduleItemUse(ClientWorld world) {
        var client = MinecraftClient.getInstance();
        var player = client.player;

        if (stage == ActionStage.SWAPPED && player != null && world.getTime() >= itemUseAtTime) {
            var action = this.action;
            if (action != null && action.match(player.getMainHandStack())) {
                assert client.interactionManager != null;
                client.interactionManager.interactItem(player, Hand.MAIN_HAND);

                if (shouldUpdateSneak) {
                    if (client.options.getSneakToggled().getValue()) {
                        client.options.sneakKey.setPressed(true);
                    } else {
                        client.options.sneakKey.setPressed(!action.sneak());
                    }
                }
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("SWAPPED -> USED, action match: {}", action);
                }
            } else {
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("SWAPPED -> USED, skipped using");
                }
            }
            stage = ActionStage.USED;
        } else if (stage == ActionStage.SWAP_BACK_SCREEN && player != null && client.currentScreen instanceof AbstractInventoryScreen<?> inv) {
            var slots = inv.getScreenHandler().slots;
            if (swapFrom < 9) {
                player.getInventory().selectedSlot = swapTo;
            } else if (swapFrom < slots.size() && inv.getScreenHandler().canInsertIntoSlot(slots.get(swapFrom))) {
                // place items back if player open inventory
                performSwap(client, inv, swapFrom, player);
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("SWAP_BACK_SCREEN -> IDLE, from {}, to {}, screen {}", swapFrom, swapTo, client.currentScreen);
                }
            } else {
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("SWAP_BACK_SCREEN -> IDLE, swap skipped");
                }
            }
            stage = ActionStage.IDLE;
        }
    }

    public void screenClosed() {
        if (stage == ActionStage.USED) {
            stage = ActionStage.SWAP_BACK_SCREEN;
            var client = MinecraftClient.getInstance();
            var player = client.player;
            if (player != null) {
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("USED -> SWAP_BACK_SCREEN");
                }
                client.setScreen(new InventoryScreen(player));
            }
        }
    }

    public void tooltip(ItemStack stack, List<Text> lines, TooltipContext cx/*? if >=1.21 >> ')'*//*, net.minecraft.item.tooltip.TooltipType _type*/) {
        var client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof AccessHandledScreen access) {
            var matched = matchAction(client.currentScreen, client.player, access.getFocusedSlot());
            if (matched != null) {
                lines.add(Text.translatable("open_in_inventory.tooltip.use"));
            }
        }
    }

    private OpenAction matchAction(@Nullable Screen screen, @Nullable PlayerEntity player, @Nullable Slot focused) {
        if (
            // basic
            stage == ActionStage.IDLE
            && screen != null
            && player != null
            && focused != null
            && !OpenInInventory.isShiftPressed(MinecraftClient.getInstance())
            // config
            && (!OpenInInventory.CONFIG.requireSingleStack() || focused.getStack().getCount() == 1)
            && (!OpenInInventory.CONFIG.requireEmptyMainHand() || player.getMainHandStack().isEmpty())
            && !OpenInInventory.isScreenBlackListed(screen)
            // container
            && focused.inventory == player.getInventory()
            && screen instanceof HandledScreen<?> handled
            && handled.getScreenHandler().getCursorStack().isEmpty()
        ) {
            return OpenInInventory.ACTION_REGISTRY.get(focused.getStack());
        }
        return null;
    }

    enum ActionStage {
        /// before anything happened
        IDLE,
        /// stack swapped, but not yet used
        SWAPPED,
        /// stack used, but not yet swapped back
        USED,
        /// the screen for swapping back stack is opened
        SWAP_BACK_SCREEN
    }
}
