package net.azisaba.lobby.util;

import org.bukkit.inventory.ItemStack;

public class NMSUtil {
    public static Object parseTag(String s) {
        try {
            return NMS.method_MojangsonParser_parse_String_void.invoke(null, s);
        } catch (Throwable throwable) {
            if (throwable instanceof VirtualMachineError) throw (VirtualMachineError) throwable;
            return null;
        }
    }

    public static Object asNMSCopy(ItemStack stack) {
        try {
            return NMS.method_CraftItemStack_asNMSCopy.invoke(null, stack);
        } catch (Throwable throwable) {
            if (throwable instanceof VirtualMachineError) throw (VirtualMachineError) throwable;
            return null;
        }
    }

    public static ItemStack asBukkitCopy(Object stack) {
        try {
            return (ItemStack) NMS.method_CraftItemStack_asBukkitCopy.invoke(null, stack);
        } catch (Throwable throwable) {
            if (throwable instanceof VirtualMachineError) throw (VirtualMachineError) throwable;
            return null;
        }
    }

    public static void setTag(Object stack, Object tag) {
        try {
            NMS.method_ItemStack_setTag.invoke(stack, tag);
        } catch (Throwable throwable) {
            if (throwable instanceof VirtualMachineError) throw (VirtualMachineError) throwable;
            if (throwable instanceof NullPointerException) throw (RuntimeException) throwable;
        }
    }

    public static ItemStack setTag(ItemStack stack, Object tag) {
        Object nmsStack = asNMSCopy(stack);
        setTag(nmsStack, tag);
        return asBukkitCopy(nmsStack);
    }
}
