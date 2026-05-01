package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class Coins implements ItemsAPI {
    private @Nullable Plugin coinsPlugin;
    private Boolean coinsInstalled;

    public boolean getIsInstalled() {
        if (coinsInstalled == null) {
            coinsPlugin = Bukkit.getPluginManager().getPlugin(getName());
            coinsInstalled = coinsPlugin != null && coinsPlugin.isEnabled();
        }

        return coinsInstalled;
    }

    public @NotNull String getName(){
        return "Coins";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final me.justeli.coins.Coins coins = (me.justeli.coins.Coins) coinsPlugin;
        assert coins != null;

        if (itemRequest.amount != null)
            result.itemStack = coins.getCreateCoin().createDropped(itemRequest.amount);
        else
            result.itemStack = coins.getCreateCoin().createDropped();

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
