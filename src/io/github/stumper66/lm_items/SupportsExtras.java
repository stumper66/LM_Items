package io.github.stumper66.lm_items;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface SupportsExtras {
    @NotNull Collection<String> getSupportedExtras();
}
