package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class ItemEdit implements ItemsAPI {
    public boolean getIsInstalled() {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    public @NotNull String getName(){
        return "ItemEdit";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        result.itemStack = emanondev.itemedit.ItemEdit.get().getServerStorage().getItem(itemRequest.itemId);

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
