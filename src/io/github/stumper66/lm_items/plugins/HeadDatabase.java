package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class HeadDatabase implements ItemsAPI {

    public boolean getIsInstalled() {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    public @NotNull String getName(){
        return "HeadDatabase";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final HeadDatabaseAPI api = new HeadDatabaseAPI();
        try{
            result.itemStack = api.getItemHead(itemRequest.itemId);
        }
        catch (Exception e){
            return result;
        }

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
