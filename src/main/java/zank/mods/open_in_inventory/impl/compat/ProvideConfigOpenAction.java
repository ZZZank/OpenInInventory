package zank.mods.open_in_inventory.impl.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.impl.DefaultOpenAction;
import zank.mods.open_in_inventory.impl.WildCardOpenAction;

import java.util.*;

/**
 * @author ZZZank
 */
public class ProvideConfigOpenAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        // preprocess
        var entries = new JsonArray();
        for (var json : OpenInInventory.CONFIG.enabledItems()) {
            if (json.isJsonPrimitive()) {
                for (var applied : registry.findAndApplyTemplate(json.getAsString())) {
                    entries.add(new JsonPrimitive(applied));
                }
            } else {
                entries.add(json);
            }
        }

        for (var json : entries) {
            DataResult<? extends OpenAction> result;
            if (json.isJsonPrimitive()) {
                result = WildCardOpenAction.CODEC.decode(JsonOps.INSTANCE, json)
                    .map(Pair::getFirst);
            } else {
                result = DefaultOpenAction.CODEC.decode(JsonOps.INSTANCE, json)
                    .map(Pair::getFirst);
            }
            result.resultOrPartial(error -> OpenInInventory.LOGGER.error(
                    "Error when parsing open action from config: {}",
                    error
                ))
                .ifPresent(action -> registry.register(action.stack(), action.sneak()));
        }
    }
}
