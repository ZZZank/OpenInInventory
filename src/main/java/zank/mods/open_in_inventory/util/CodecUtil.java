package zank.mods.open_in_inventory.util;

import com.mojang.serialization.DataResult;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public abstract class CodecUtil {
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
