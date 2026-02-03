package zank.mods.open_in_inventory.impl.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.impl.OpenActionImpl;

import java.util.*;

/**
 * @author ZZZank
 */
public class ProvideConfigOpenAction implements OpenInInventoryPlugin {

    @Override
    public void registerAction(OpenActionRegistry registry) {
        for (var jsonElement : OpenInInventoryConfig.ENABLED_ITEMS) {
            try {
                for (var json : unzipTemplate(registry, normalizeToObject(jsonElement))) {
                    var parsed = OpenInInventory.GSON.fromJson(json, OpenActionImpl.class);
                    registry.register(parsed.stack(), parsed.sneak());
                }
            } catch (Exception e) {
                OpenInInventory.LOGGER.error("Error when parsing open action from config", e);
            }
        }
    }

    public static JsonObject normalizeToObject(JsonElement json) {
        if (json.isJsonPrimitive()) {
            var result = new JsonObject();
            result.add("id", json);
            return result;
        }
        return json.getAsJsonObject();
    }

    public static Collection<JsonObject> unzipTemplate(OpenActionRegistry registry, JsonObject json) {
        var id = json.get("id").getAsJsonPrimitive().getAsString();
        var replaced = registry.findAndApplyTemplate(id);
        if (replaced.size() == 1) {
            return List.of(json);
        }
        return replaced.stream()
            .map(JsonPrimitive::new)
            .map(primitive -> {
                var result = json.deepCopy();
                result.add("id", primitive);
                return result;
            })
            .toList();
    }
}
