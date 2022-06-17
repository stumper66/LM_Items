package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unused")
public interface ItemsAPI {
    boolean getIsInstalled();

    @NotNull String getName();

    @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest);

    @NotNull Collection<String> getItemTypes();
}
