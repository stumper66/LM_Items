package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings("unused")
public interface ItemsAPI {
    boolean getIsInstalled();

    @NotNull String getName();

    @NotNull GetItemResult getItem(final @Nullable String type, final @NotNull String itemId);

    @NotNull GetItemResult getItem(final @Nullable String type, final @NotNull String itemId, final double amount);

    @NotNull Collection<String> getItemTypes();
}
