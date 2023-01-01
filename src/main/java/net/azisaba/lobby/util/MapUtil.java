package net.azisaba.lobby.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {
    public static <K1, V1, K2, V2> @NotNull Map<K2, V2> map(
            @NotNull Map<K1, V1> map,
            @NotNull Function<K1, K2> keyFunction,
            @NotNull Function<V1, V2> valueFunction
    ) {
        return map.entrySet().stream().collect(Collectors.toMap(e -> keyFunction.apply(e.getKey()), e -> valueFunction.apply(e.getValue())));
    }
}
