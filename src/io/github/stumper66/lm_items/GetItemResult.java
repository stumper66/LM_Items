package io.github.stumper66.lm_items;

import org.bukkit.inventory.ItemStack;

public class GetItemResult {
    public GetItemResult(){}

    public GetItemResult(final boolean pluginIsInstalled){
        this.pluginIsInstalled = pluginIsInstalled;
    }

    public boolean pluginIsInstalled;
    public ItemStack itemStack;

    public boolean typeIsNotSupported;
}
