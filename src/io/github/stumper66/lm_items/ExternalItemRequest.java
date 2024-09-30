package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ExternalItemRequest {
    public ExternalItemRequest(final @NotNull String itemId) {
        this.itemId = itemId;
        this.minItems = 1;
        this.maxItems = 1;
    }

    public final @NotNull String itemId;
    public @Nullable String itemType;
    public Double amount;
    public @Nullable Map<String, Object> extras;
    // LMI 1.3.0 added fields:
    public boolean getMultipleItems;
    public boolean isDebugEnabled; // for possible future use
    public @Nullable List<String> allowedList;
    public @Nullable List<String> excludedList;
    public int minItems;
    public int maxItems;
}
