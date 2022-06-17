package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ExternalItemRequest {
    public ExternalItemRequest(final @NotNull String itemId){
        this.itemId = itemId;
    }

    public final @NotNull String itemId;
    public @Nullable String itemType;
    public Double amount;
    public @Nullable Map<String, Object> extras;
}
