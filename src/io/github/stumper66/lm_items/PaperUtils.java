package io.github.stumper66.lm_items;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaperUtils {
    public static @NotNull String getDisplayName(final @NotNull ItemStack itemStack){
        final String displayName = PlainTextComponentSerializer.plainText().serialize(itemStack.displayName());
        // shows up as [DisplayName]
        return displayName.substring(1, displayName.length() - 1);
    }
}
