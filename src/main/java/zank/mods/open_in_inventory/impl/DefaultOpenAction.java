package zank.mods.open_in_inventory.impl;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.util.CodecGsonAdapter;
import zank.mods.open_in_inventory.util.SimpleGsonSerde;

/**
 * @author ZZZank
 */
@JsonAdapter(DefaultOpenAction.GsonAdapter.class)
public record DefaultOpenAction(ItemStack stack, boolean sneak) implements OpenAction {

    public static class GsonAdapter implements SimpleGsonSerde<DefaultOpenAction> {
        private static final String COUNT_KEY = /*? if <1.21 { */"Count"/*? } else {*//*"count"*//*?}*/;
        private static final Integer COUNT_DEFAULT = 1;

        @Override
        public JsonElement serialize(DefaultOpenAction value, JsonSerializationContext cx) {
            var json = (JsonObject) CodecGsonAdapter.getOrThrow(
                ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, value.stack),
                IllegalArgumentException::new
            );

            var count = json.get(COUNT_KEY);
            if (count != null && count.getAsInt() == COUNT_DEFAULT) {
                json.remove(COUNT_KEY);
            }

            if (value.sneak != SNEAK_DEFAULT) {
                json.addProperty("sneak", value.sneak);
            }

            return json;
        }

        @Override
        public DefaultOpenAction deserialize(JsonElement json, JsonDeserializationContext cx)
            throws JsonParseException {
            var object = json.getAsJsonObject();
            if (!object.has(COUNT_KEY)) {
                object.addProperty(COUNT_KEY, COUNT_DEFAULT);
            }

            var stack = CodecGsonAdapter.getOrThrow(
                ItemStack.CODEC.decode(JsonOps.INSTANCE, object),
                JsonParseException::new
            ).getFirst();

            var sneakJson = object.get("sneak");
            var sneak = sneakJson == null ? SNEAK_DEFAULT : sneakJson.getAsBoolean();

            return new DefaultOpenAction(stack, sneak);
        }
    }
}
