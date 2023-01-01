package net.azisaba.lobby.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class VoidExecutor {
    public abstract void execute();

    public static void safeExecute(@NotNull Supplier<VoidExecutor> supplier) {
        supplier.get().execute();
    }
}
