package zank.mods.open_in_inventory.impl.handler;

import dev.architectury.event.CompoundEventResult;
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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
//? if >1.21
//import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.mixin.AccessHandledScreen;

import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class ActionHandler {
    /// we assume that both [#swapFrom] and [#swapTo] is targeting [InventoryScreen]
    private int swapFrom;
    /// we assume that both [#swapFrom] and [#swapTo] is targeting [InventoryScreen]
    private int swapTo;

    private ActionStage stage = ActionStage.IDLE;
    private long itemUseAtTime = -1;
    private OpenAction openAction = null;

    public EventResult beforeMouseClicked(
        MinecraftClient client,
        Screen _screen,
        double mouseX,
        double mouseY,
        int button
    ) {
        if (stage != ActionStage.IDLE) {
            return EventResult.pass();
        }

        // Right-click without Shift
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT || OpenInInventory.isShiftPressed(client)) {
            return EventResult.pass();
        }

        var player = client.player;
        var world = client.world;
        if (player != null && world != null && _screen instanceof HandledScreen<?> screen) {
            if (OpenInInventory.isScreenBlackListed(screen)) {
                return EventResult.pass();
            }

            var focused = ((AccessHandledScreen) screen).getFocusedSlot();
            if (
                // mouse on something
                focused != null
                // mouse not holding anything
                && screen.getScreenHandler().getCursorStack().isEmpty()
                // mouse in player inventory
                && focused.inventory == player.getInventory()
                // target slot is free
                && (!OpenInInventoryConfig.REQUIRE_EMPTY_MAIN_HAND || player.getMainHandStack().isEmpty())
                && screen.getScreenHandler().canInsertIntoSlot(focused)
            ) {
                swapFrom = focused.getIndex();
                swapTo = player.getInventory().selectedSlot;

                var oldFocusedStack = focused.getStack();
                var action = OpenInInventory.ACTIONS.get(oldFocusedStack);
                if (action == null) {
                    return EventResult.pass();
                }

                if (OpenInInventoryConfig.DEBUG) {
                    OpenInInventory.LOGGER.info(
                        "Attempt to swap slots {} with id {} with hotbar {} in gui {}",
                        focused.getIndex(),
                        focused.id,
                        swapTo,
                        screen.getClass().getName()
                    );
                }

                /// the screen is not always [InventoryScreen], so we sometimes use [net.minecraft.screen.slot.Slot#id]
                /// (relative to [net.minecraft.screen.ScreenHandler]) instead of [net.minecraft.screen.slot.Slot#getIndex()]
                var actualSwapFrom = screen instanceof AbstractInventoryScreen
                    ? focused.getIndex()
                    : focused.id;
                client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    actualSwapFrom,
                    swapTo,
                    SlotActionType.SWAP,
                    player
                );

                if (player.getMainHandStack() != oldFocusedStack) {
                    return EventResult.pass();
                }

                client.setScreen(null);

                itemUseAtTime = world.getTime() + OpenInInventoryConfig.OPEN_DELAY;
                stage = ActionStage.SWAPPED;
                openAction = action;

                return EventResult.interruptTrue();
            }
        }
        return EventResult.pass();
    }

    public void scheduleItemUse(ClientWorld world) {
        var client = MinecraftClient.getInstance();
        var player = client.player;

        if (stage == ActionStage.SWAPPED && player != null && world.getTime() >= itemUseAtTime) {
            var action = openAction;
            if (action != null && action.match(player.getMainHandStack())) {
                var shouldSneak = action.sneak();
                var sneaking = player.isSneaking();

                if (shouldSneak != sneaking) {
                    player.input.sneaking = shouldSneak;
                    client.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(player.sidewaysSpeed, player.forwardSpeed, player.input.jumping, shouldSneak));

                    client.interactionManager.interactItem(player, Hand.MAIN_HAND);

                    player.input.sneaking = sneaking;
                    client.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(player.sidewaysSpeed, player.forwardSpeed, player.input.jumping, sneaking));
                } else {
                    client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                }
            }
            stage = ActionStage.USED;
        } else if (stage == ActionStage.SWAP_BACK_SCREEN && client.currentScreen instanceof InventoryScreen inv) {
            // place items back if player open inventory
            client.interactionManager.clickSlot(
                inv.getScreenHandler().syncId,
                swapFrom,
                swapTo,
                SlotActionType.SWAP,
                client.player
            );
            stage = ActionStage.IDLE;
        }
    }

    public CompoundEventResult<Screen> onScreenChange(Screen screen) {
        if (stage == ActionStage.USED && screen == null) {
            stage = ActionStage.SWAP_BACK_SCREEN;
            var client = MinecraftClient.getInstance();
            var player = Objects.requireNonNull(client.player);

            return CompoundEventResult.interruptTrue(new InventoryScreen(player));
        }
        return CompoundEventResult.pass();
    }

    //? if <1.21 {
    public void tooltip(ItemStack stack, List<Text> lines, TooltipContext flag) {
    //? } else
    //public void tooltip(ItemStack stack, List<Text> lines, TooltipContext flag, TooltipType tooltipType) {
        var screen = MinecraftClient.getInstance().currentScreen;
        var player = MinecraftClient.getInstance().player;
        if (
            player != null
            && player.getInventory().getMainHandStack() != stack
            && screen != null
            && !OpenInInventory.isScreenBlackListed(screen)
            && screen instanceof AccessHandledScreen access
            && access.getFocusedSlot() != null
            && access.getFocusedSlot().inventory == player.getInventory()
            && OpenInInventory.ACTIONS.get(stack) != null
        ) {
            lines.add(Text.translatable("open_in_inventory.tooltip.use"));
        }
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
