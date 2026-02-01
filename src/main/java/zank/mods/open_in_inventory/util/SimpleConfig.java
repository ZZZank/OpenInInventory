package zank.mods.open_in_inventory.util;

import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author ZZZank
 */
public class SimpleConfig {

    private JsonObject toRead = new JsonObject();
    private JsonObject toWrite = new JsonObject();

    public void reset() {
        toRead = new JsonObject();
        toWrite = new JsonObject();
    }

    public void read(Gson gson, Reader reader) {
        toRead = gson.fromJson(reader, JsonObject.class);
    }

    public void write(Gson gson, Writer writer) {
        gson.toJson(toWrite, writer);
    }

    public void read(Gson gson, Path path) throws IOException {
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                read(gson, reader);
            }
        }
    }

    public void write(Gson gson, Path path) throws IOException {
        try (var writer = Files.newBufferedWriter(path)) {
            write(gson, writer);
        }
    }

    public void addElementToWrite(String key, JsonElement json) {
        toWrite.add(key, json);
    }

    public JsonElement getJson(String key, JsonElement fallback, String comment) {
        var got = toRead.get(key);
        if (got == null) {
            got = fallback;
        }
        if (comment != null) {
            var lines = comment.split("\n");
            if (lines.length > 1) {
                var comments = new JsonArray();
                Arrays.asList(lines).forEach(comments::add);
                toWrite.add("//" + key, comments);
            } else {
                toWrite.addProperty("//" + key, comment);
            }
        }
        toWrite.add(key, got);
        return got;
    }

    public JsonElement getJson(String key, JsonElement fallback) {
        return getJson(key, fallback, null);
    }

    public boolean getBool(String key, boolean fallback, String comment) {
        return getJson(key, new JsonPrimitive(fallback), comment).getAsBoolean();
    }

    public boolean getBool(String key, boolean fallback) {
        return getJson(key, new JsonPrimitive(fallback)).getAsBoolean();
    }

    public int getInt(String key, int fallback, String comment) {
        return getJson(key, new JsonPrimitive(fallback), comment).getAsInt();
    }

    public int getInt(String key, int fallback) {
        return getJson(key, new JsonPrimitive(fallback)).getAsInt();
    }
}