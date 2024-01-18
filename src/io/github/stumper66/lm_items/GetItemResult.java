package io.github.stumper66.lm_items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GetItemResult {
    public GetItemResult(){}

    public GetItemResult(final boolean pluginIsInstalled){
        this.pluginIsInstalled = pluginIsInstalled;
    }

    public boolean pluginIsInstalled;
    public ItemStack itemStack;
    public @Nullable Collection<ItemStack> itemStacks;
    public @Nullable String debugResults; // for possible future use
    public boolean typeIsNotSupported;
}
