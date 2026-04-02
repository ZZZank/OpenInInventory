package zank.mods.open_in_inventory.util;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author ZZZank
 */
public interface SimpleGsonSerde<T> extends JsonSerializer<T>, JsonDeserializer<T> {

    JsonElement serialize(T value, JsonSerializationContext cx);

    @Override
    default JsonElement serialize(T value, Type type, JsonSerializationContext cx) {
        return serialize(value, cx);
    }

    T deserialize(JsonElement json, JsonDeserializationContext cx) throws JsonParseException;

    @Override
    default T deserialize(JsonElement json, Type type, JsonDeserializationContext cx) throws JsonParseException {
        return deserialize(json, cx);
    }
}
