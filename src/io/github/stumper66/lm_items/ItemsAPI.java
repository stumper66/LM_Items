package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@SuppressWarnings("unused")
public interface ItemsAPI {
    boolean getIsInstalled();

    @NotNull String getName();

    @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest);

    @NotNull Collection<String> getItemTypes();

    default int apiVersion(){
        return 2;
    }
}
