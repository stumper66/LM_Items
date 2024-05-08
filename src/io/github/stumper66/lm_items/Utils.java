package io.github.stumper66.lm_items;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.logging.Logger;

public class Utils {
    public static final Logger logger = Bukkit.getLogger();
    private static Boolean hasKyori;

    private static boolean checkForKyori(){
        if (hasKyori != null) return hasKyori;

        try {
            Class.forName("net.kyori.adventure.text.Component");
            hasKyori = true;
            return true;
        } catch (ClassNotFoundException ignored) { }

        hasKyori = false;
        return false;
    }

    public static @Nullable String getStringValue(final @NotNull Map<String, Object> extras, final @NotNull String keyName){
        if (!extras.containsKey(keyName)) return null;

        final Object temp = extras.get(keyName);
        if (temp == null)
            return null;
        else
            return temp.toString();
    }

    public static int getIntValue(final @NotNull Map<String, Object> extras, final @NotNull String keyName, final int defaultValue){
        if (!extras.containsKey(keyName)) {
            return defaultValue;
        }

        final Object temp = extras.get(keyName);
        if (temp == null) return defaultValue;

        try{
            return Integer.parseInt(temp.toString());
        }
        catch (Exception ignored) {}

        return defaultValue;
    }

    @SuppressWarnings("deprecation")
    public static @NotNull String getDisplayName(final @NotNull ItemStack itemStack){
        if (checkForKyori()){
            return PaperUtils.getDisplayName(itemStack);
        }
        else {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) return "";
            return itemMeta.getDisplayName();
        }
    }
}
