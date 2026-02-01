package zank.mods.open_in_inventory.impl.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.DyeColor;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.impl.OpenActionImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ZZZank
 */
public class ReadOpenActionFromConfig implements OpenInInventoryPlugin {
    private static final Map<String, List<String>> TEMPLATES = Map.of(
        "color", Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList()
    );

    @Override
    public void registerAction(OpenActionRegistry registry) {
        for (var jsonElement : OpenInInventoryConfig.ENABLED_ITEMS) {
            try {
                var parsed = parseAction(jsonElement);
                for (var action : parsed) {
                    registry.register(action.stack(), action.sneak());
                }
            } catch (Exception e) {
                OpenInInventory.LOGGER.error("Error when parsing open action from config", e);
            }
        }
    }

    private static List<OpenAction> parseAction(JsonElement json) {
        var parsed = new ArrayList<OpenAction>();

        if (json.isJsonPrimitive()) {
            for (var replaced : replaceTemplate(json.getAsString())) {
                parsed.add(OpenInInventory.GSON.fromJson(new JsonPrimitive(replaced), OpenActionImpl.class));
            }
        } else {
            var jsonObject = json.getAsJsonObject();
            for (var replacedId : replaceTemplate(jsonObject.get("id").getAsString())) {
                jsonObject.addProperty("id", replacedId);
                parsed.add(OpenInInventory.GSON.fromJson(jsonObject, OpenActionImpl.class));
            }
        }

        return parsed;
    }

    private static List<String> replaceTemplate(String original) {
        // example: some_mod:{color}_bag

        var left = original.indexOf('{');
        if (left < 0) {
            return List.of(original);
        }

        var right = original.indexOf('}', left);
        if (right < 0) {
            throw new IllegalArgumentException("Found '{', but no matching '}' in string: " + original);
        }

        var before = original.substring(0, left); // some_mod:
        var after = original.substring(right + 1); // _bag
        var template = original.substring(left + 1, right); // color

        var replaceWith = TEMPLATES.get(template);
        if (replaceWith == null) {
            throw new IllegalArgumentException("Unknown template: " + template);
        }

        var list = new ArrayList<String>();
        for (var replaced : replaceWith) {
            list.addAll(replaceTemplate(before + replaced + after));
        }
        return list;
    }
}
