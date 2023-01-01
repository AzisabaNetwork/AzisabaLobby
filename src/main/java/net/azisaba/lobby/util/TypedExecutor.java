package net.azisaba.lobby.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class TypedExecutor<T> {
    public abstract T execute();

    public static <T> T safeExecute(@NotNull Supplier<TypedExecutor<T>> supplier) {
        return supplier.get().execute();
    }
}
