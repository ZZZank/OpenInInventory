package zank.mods.open_in_inventory.impl;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;

import java.lang.reflect.Type;

/**
 * @author ZZZank
 */
@JsonAdapter(OpenActionImpl.GsonAdapter.class)
public record OpenActionImpl(ItemStack stack, boolean sneak) implements OpenAction {
    private static final boolean SNEAK_DEFAULT = false;
    private static final Integer COUNT_DEFAULT = (Integer) 1;

    public static class GsonAdapter implements JsonDeserializer<OpenActionImpl> {
        private static final String COUNT_KEY = /*? if <1.21 { */"Count"/*? } else {*//*"count"*//*?}*/;

        @Override
        public OpenActionImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
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
    }
}
