package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LM_Items_API {
    boolean doesSupportPlugin(@NotNull final String pluginName);

    @Nullable ItemsAPI getItemAPIForPlugin(final @NotNull String pluginName);
}
