package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class EcoAPI implements ItemsAPI {
    public EcoAPI() {
        this.isInstalled = Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    private final boolean isInstalled;

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "eco";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        result.itemStack = com.willfp.eco.core.items.Items.lookup(itemRequest.itemId.toLowerCase()).getItem();

        if (result.itemStack.getType() == Material.AIR)
            result.itemStack = null;

        return result;
    }
    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
