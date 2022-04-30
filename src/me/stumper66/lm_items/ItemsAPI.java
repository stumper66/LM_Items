package me.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemsAPI {
    boolean getIsInstalled();

    @NotNull GetItemResult getItem(final @Nullable String type, final @NotNull String itemId);

    @NotNull List<String> getItemTypes();
}
