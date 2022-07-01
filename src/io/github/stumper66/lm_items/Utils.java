package io.github.stumper66.lm_items;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.logging.Logger;

public class Utils {
    public static final Logger logger = Bukkit.getLogger();

    public static @Nullable String getValueString(final @NotNull Map<String, Object> extras, final @NotNull String keyName){
        if (!extras.containsKey(keyName)) return null;

        final Object temp = extras.get(keyName);
        if (temp == null)
            return null;
        else
            return temp.toString();
    }

    public static int getIntValue(final @NotNull Map<String, Object> extras, final @NotNull String keyName, final int defaultValue){
        if (!extras.containsKey(keyName))
            return defaultValue;

        final Object temp = extras.get(keyName);
        if (temp instanceof Integer)
            return (int) temp;
        else if (temp instanceof Double){
            return (int)((double) temp);
        }

        return defaultValue;
    }
}
