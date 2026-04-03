package zank.mods.open_in_inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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
    @SerializedName("screen_blacklist") Set<String> screenBlacklist,
    @SerializedName("require_enpty_main_hand") boolean requireEmptyMainHand,
    @SerializedName("require_single_stack") boolean requireSingleStack,
    @SerializedName("open_delay") int openDelay,
    @SerializedName("debug") boolean debug,
    @SerializedName("enabled_items") JsonArray enabledItems
) {
    public static final String LANG_PREFIX = OpenInInventory.ID + ".config.";

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

        json.addProperty("//", I18n.translate(LANG_PREFIX + "refresh"));
        for (var recordComponent : OpenInInventoryConfig.class.getRecordComponents()) {
            // .getAccessor() because @SerializedName didn't have ElementType.RECORD_COMPONENT target
            var name = recordComponent.getAccessor()
                .getAnnotation(SerializedName.class)
                .value();

            var commentStr = I18n.translate(LANG_PREFIX + name);
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
