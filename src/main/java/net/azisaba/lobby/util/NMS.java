package net.azisaba.lobby.util;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NMS {
    public static final Class<?> class_nms_MojangsonParser;
    public static final Class<?> class_obc_CraftItemStack;
    public static final Class<?> class_nms_ItemStack;
    public static final Class<?> class_nms_NBTTagCompound;

    public static final Method method_MojangsonParser_parse_String_void;
    public static final Method method_CraftItemStack_asNMSCopy;
    public static final Method method_CraftItemStack_asBukkitCopy;
    public static final Method method_ItemStack_setTag;

    static {
        try {
            class_nms_MojangsonParser = Class.forName("net.minecraft.server.v1_12_R1.MojangsonParser");
            class_obc_CraftItemStack = Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack");
            class_nms_ItemStack = Class.forName("net.minecraft.server.v1_12_R1.ItemStack");
            class_nms_NBTTagCompound = Class.forName("net.minecraft.server.v1_12_R1.NBTTagCompound");
            method_MojangsonParser_parse_String_void = class_nms_MojangsonParser.getMethod("parse", String.class);
            method_CraftItemStack_asNMSCopy = class_obc_CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            method_CraftItemStack_asBukkitCopy = class_obc_CraftItemStack.getMethod("asBukkitCopy", class_nms_ItemStack);
            method_ItemStack_setTag = class_nms_ItemStack.getMethod("setTag", class_nms_NBTTagCompound);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
