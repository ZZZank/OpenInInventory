package zank.mods.open_in_inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ZZZank
 */
public record OpenInInventoryConfig(
    Set<String> screenBlacklist,
    boolean requireEmptyMainHand,
    boolean requireSingleStack,
    int openDelay,
    boolean debug,
    JsonArray enabledItems
) {
    public OpenInInventoryConfig() {
        this(
            new HashSet<>(),
            true,
            true,
            3,
            false,
            new JsonArray()
        );
    }

    public void write(Path configFile) throws IOException {
        var json = (JsonObject) OpenInInventory.GSON.toJsonTree(this);

        for (var recordComponent : OpenInInventoryConfig.class.getRecordComponents()) {
            var name = recordComponent.getName();

            var commentStr = I18n.translate(OpenInInventory.ID + ".config." + name);
            if (commentStr.indexOf('\n') < 0) {
                json.addProperty("//" + name, commentStr);
            } else {
                json.add("//" + name, OpenInInventory.GSON.toJsonTree(commentStr.split("\n")));
            }
        }

        try (var writer = Files.newBufferedWriter(configFile)) {
            OpenInInventory.GSON.toJson(json, writer);
        }
    }
}
