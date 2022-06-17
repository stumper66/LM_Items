package io.github.stumper66.lm_items.plugins.ecosupported;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EcoCrates implements ItemsAPI {
    public EcoCrates(){
        checkDeps();
    }

    private boolean isInstalled;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "eco")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "EcoCrates";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        result.itemStack = com.willfp.eco.core.items.Items.lookup(
                String.format("%s:%s", getName().toLowerCase(), itemRequest.itemId.toLowerCase())
        ).getItem();

        if (result.itemStack.getType() == Material.AIR)
            result.itemStack = null;

        return result;
    }

    public @NotNull Collection<String> getItemTypes(){
        return Collections.emptyList();
    }
}
