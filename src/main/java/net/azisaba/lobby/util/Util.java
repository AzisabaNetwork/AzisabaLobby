package net.azisaba.lobby.util;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

public class Util {
    public static boolean isClassPresent(
            @NotNull
            @Language(value = "JAVA", prefix = "class X{void x(){Class.forName(\"", suffix = "\");}}")
            String className
    ) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
