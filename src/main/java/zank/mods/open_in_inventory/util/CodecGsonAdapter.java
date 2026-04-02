package zank.mods.open_in_inventory.util;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public record CodecGsonAdapter<A>(Codec<A> codec) implements JsonSerializer<A>, JsonDeserializer<A> {
    @Override
    public A deserialize(JsonElement json, Type type, JsonDeserializationContext cx) throws JsonParseException {
        return getOrThrow(codec.decode(JsonOps.INSTANCE, json), JsonParseException::new).getFirst();
    }

    @Override
    public JsonElement serialize(A obj, Type type, JsonSerializationContext cx) {
        return getOrThrow(codec.encodeStart(JsonOps.INSTANCE, obj), IllegalArgumentException::new);
    }

    public static <R> R getOrThrow(DataResult<R> result, Function<? super String, ? extends RuntimeException> error) {
        var errorCapture = new ErrorCapture();
        var optional = result.resultOrPartial(errorCapture);
        if (errorCapture.err != null) {
            throw error.apply(errorCapture.err);
        }
        return optional.orElseThrow();
    }

    private static final class ErrorCapture implements Consumer<String> {
        private String err = null;

        @Override
        public void accept(String s) {
            err = s;
        }
    }
}
