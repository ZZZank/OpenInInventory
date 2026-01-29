package zank.mods.open_in_inventory.impl;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;

import java.lang.reflect.Type;

/**
 * @author ZZZank
 */
@JsonAdapter(OpenActionImpl.GsonAdapter.class)
public record OpenActionImpl(ItemStack stack, boolean sneak) implements OpenAction {
    private static final boolean SNEAK_DEFAULT = false;
    private static final int COUNT_DEFAULT = 1;

    public static class GsonAdapter implements JsonSerializer<OpenActionImpl>, JsonDeserializer<OpenActionImpl> {
        private static final String COUNT_KEY = /*? if <1.21 { */"Count"/*? } else {*//*"count"*//*?}*/;

        @Override
        public OpenActionImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                var stack = Registries.ITEM
                    .get(Identifier.tryParse(jsonElement.getAsString()))
                    .getDefaultStack();
                return new OpenActionImpl(stack, false);
            }

            var json = jsonElement.getAsJsonObject();
            if (!json.has(COUNT_KEY)) {
                json.addProperty(COUNT_KEY, COUNT_DEFAULT);
            }

            var stack = ItemStack.CODEC.decode(JsonOps.INSTANCE, json)
                .resultOrPartial(OpenInInventory.LOGGER::error)
                .orElseThrow()
                .getFirst();

            var sneakJson = json.get("sneak");
            var sneak = sneakJson == null ? SNEAK_DEFAULT : sneakJson.getAsBoolean();

            return new OpenActionImpl(stack, sneak);
        }

        @Override
        public JsonElement serialize(OpenActionImpl action, Type type, JsonSerializationContext context) {
            var json = ItemStack.CODEC
                .encodeStart(JsonOps.INSTANCE, action.stack)
                .resultOrPartial(OpenInInventory.LOGGER::error)
                .orElseThrow()
                .getAsJsonObject();
            if (action.sneak != SNEAK_DEFAULT) {
                json.addProperty("sneak", action.sneak);
            }
            if (json.has(COUNT_KEY) && json.get(COUNT_KEY).getAsInt() == COUNT_DEFAULT) {
                json.remove(COUNT_KEY);
            }
            return json;
        }
    }
}
