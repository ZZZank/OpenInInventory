package zank.mods.open_in_inventory.impl.handler;

import dev.architectury.event.EventResult;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.mixin.AccessHandledScreen;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
        Minecraft client,
        Screen _screen,
        //$ if <26.1 'double mouseX, double mouseY, int button' else 'net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick'
        double mouseX, double mouseY, int button
    ) {
        //? if >= 26.1
        //var button = event.button();
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return EventResult.pass();
        }

        var player = client.player;
        var world = client.level;
        if (player == null || world == null || !(_screen instanceof AbstractContainerScreen<?> screen)) {
            return EventResult.pass();
        }

        var focused = ((AccessHandledScreen) screen).getHoveredSlot();
        var matched = matchAction(screen, player, focused);
        if (matched == null) {
            return EventResult.pass();
        }

        swapFrom = focused.getContainerSlot();
        swapTo = player.getInventory()
            //$ if <26.1 '.selected;' else '.getSelectedSlot();'
            .selected;

        var stackBeforeSwap = focused.getItem();

        if (OpenInInventory.CONFIG.debug()) {
            OpenInInventory.LOGGER.info(
                "IDLE -> SWAPPED, attempt to swap slot(index {}, id {}) with hotbar {} in gui {}",
                focused.getContainerSlot(),
                focused.index,
                swapTo,
                screen
            );
        }

        ((AccessHandledScreen) screen).invokeSlotClicked(
            focused,
            focused.index,
            swapTo,
            //$ if <26.1 'net.minecraft.world.inventory.ClickType.SWAP' else 'net.minecraft.world.inventory.ContainerInput.SWAP'
            net.minecraft.world.inventory.ClickType.SWAP
        );
        if (player.getMainHandItem() != stackBeforeSwap) {
            return EventResult.pass();
        }

        shouldUpdateSneak = matched.sneak() != client.options.keyShift.isDown();
        if (shouldUpdateSneak) {
            if (client.options.toggleCrouch().get()) {
                client.options.keyShift.setDown(true);
            } else {
                client.options.keyShift.setDown(matched.sneak());
            }
        }

        itemUseAtTime = world.getGameTime() + OpenInInventory.CONFIG.openDelay();
        stage = ActionStage.SWAPPED;
        action = matched;

        return EventResult.interruptFalse();
    }

    public void tick(ClientLevel world) {
        var client = Minecraft.getInstance();
        var player = client.player;

        if (stage == ActionStage.SWAPPED && player != null && world.getGameTime() >= itemUseAtTime) {
            var action = this.action;
            if (action != null && action.match(player.getMainHandItem())) {
                // player.closeContainer();
                // ^ can be used for clearing container menu, so that when we open a chest after
                // SWAP_BACK stage, it will not revert the inventory back to SWAPPED

                // the two lines below is for simulating .closeContainer() without closing screen
                // because closing screen will grab the mouse cursor, which is an awful experience
                player.connection.send(new ServerboundContainerClosePacket(player.containerMenu.containerId));
                player.containerMenu = player.inventoryMenu;

                assert client.gameMode != null;
                client.gameMode.useItem(player, InteractionHand.MAIN_HAND);

                if (shouldUpdateSneak) {
                    if (client.options.toggleCrouch().get()) {
                        client.options.keyShift.setDown(true);
                    } else {
                        client.options.keyShift.setDown(!action.sneak());
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
        } else if (stage == ActionStage.SWAP_BACK_SCREEN && player != null && client.screen instanceof AbstractContainerScreen<?> screen) {
            Slot swapTarget = null;
            for (Slot slot : screen.getMenu().slots) {
                if (slot.getContainerSlot() == this.swapFrom) {
                    swapTarget = slot;
                    break;
                }
            }

            if (swapTarget != null) {
                ((AccessHandledScreen) screen).invokeSlotClicked(
                    swapTarget,
                    swapTarget.index,
                    swapTo,
                    //$ if <26.1 'net.minecraft.world.inventory.ClickType.SWAP' else 'net.minecraft.world.inventory.ContainerInput.SWAP'
                    net.minecraft.world.inventory.ClickType.SWAP
                );
            } else {
                // TODO: debug log
            }
            stage = ActionStage.IDLE;
        }
    }

    public void screenClosed(Minecraft client) {
        if (stage == ActionStage.USED) {
            stage = ActionStage.SWAP_BACK_SCREEN;
            var player = client.player;
            if (player != null) {
                if (OpenInInventory.CONFIG.debug()) {
                    OpenInInventory.LOGGER.info("USED -> SWAP_BACK_SCREEN");
                }
                client.setScreen(new InventoryScreen(player));
            }
        }
    }

    public void tooltip(
        ItemStack stack,
        List<Component> lines,
        //? if > 1.21
        //net.minecraft.world.item.Item.TooltipContext ignored1,
        TooltipFlag ignored2
    ) {
        var client = Minecraft.getInstance();
        if (client.screen instanceof AccessHandledScreen access) {
            var matched = matchAction(client.screen, client.player, access.getHoveredSlot());
            if (matched != null) {
                lines.add(Component.translatable("open_in_inventory.tooltip.use"));
            }
        }
    }

    private OpenAction matchAction(@Nullable Screen screen, @Nullable Player player, @Nullable Slot focused) {
        if (
            // basic
            stage == ActionStage.IDLE
            && screen != null
            && player != null
            && focused != null
            && !OpenInInventory.isShiftPressed(Minecraft.getInstance())
            // config
            && (!OpenInInventory.CONFIG.requireSingleStack() || focused.getItem().getCount() == 1)
            && (!OpenInInventory.CONFIG.requireEmptyMainHand() || player.getMainHandItem().isEmpty())
            && !OpenInInventory.isScreenBlackListed(screen)
            // container
            && focused.container == player.getInventory()
            && screen instanceof AbstractContainerScreen<?> handled
            && handled.getMenu().getCarried().isEmpty()
        ) {
            return OpenInInventory.ACTION_REGISTRY.get(focused.getItem());
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
