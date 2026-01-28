package zank.mods.open_in_inventory;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
@JsonAdapter(OpenAction.GsonAdapter.class)
public record OpenAction(ItemStack stack, boolean sneakWhenUse) {
    public static final Map<Item, List<OpenAction>> REGISTRY = new HashMap<>();

    public static void register(ItemStack stack, boolean sneak) {
        REGISTRY.computeIfAbsent(stack.getItem(), k -> new ArrayList<>(3))
            .add(new OpenAction(stack, sneak));
    }

    @Nullable
    public static OpenAction get(ItemStack stack) {
        if (OpenInInventoryConfig.REQUIRE_SINGLE_STACK && stack.getCount() != 1) {
            return null;
        }

        for (var action : REGISTRY.getOrDefault(stack.getItem(), List.of())) {
            if (action.match(stack)) {
                return action;
            }
        }
        return null;
    }

    public boolean match(ItemStack stack) {
        var match = this.stack;
        //? if <1.21 {
        if (match.hasNbt()) {
        //? } else
        //if (!match.getComponentChanges().isEmpty()) {
            return ItemStack.areEqual(match, stack);
        } else {
            return stack.isOf(match.getItem());
        }
    }

    public static class GsonAdapter implements JsonSerializer<OpenAction>, JsonDeserializer<OpenAction> {
        private static final String COUNT_KEY = /*? if <1.21 { */"Count"/*? } else {*//*"count"*//*?}*/;

        @Override
        public OpenAction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
            var json = jsonElement.getAsJsonObject();
            if (!json.has(COUNT_KEY)) {
                json.addProperty(COUNT_KEY, 1);
            }
            var stack = ItemStack.CODEC.decode(JsonOps.INSTANCE, json)
                .resultOrPartial(OpenInInventory.LOGGER::error)
                .orElseThrow()
                .getFirst();
            var sneak = json.get("sneak").getAsBoolean();
            return new OpenAction(stack, sneak);
        }

        @Override
        public JsonElement serialize(OpenAction action, Type type, JsonSerializationContext context) {
            var json = ItemStack.CODEC
                .encodeStart(JsonOps.INSTANCE, action.stack)
                .resultOrPartial(OpenInInventory.LOGGER::error)
                .orElseThrow()
                .getAsJsonObject();
            json.addProperty("sneak", action.sneakWhenUse);
            return json;
        }
    }
}
