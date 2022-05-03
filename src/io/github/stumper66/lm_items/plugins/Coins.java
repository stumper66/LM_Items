package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class Coins implements ItemsAPI {
    public boolean getIsInstalled() {
        return Bukkit.getPluginManager().getPlugin("coins") != null;
    }

    public @NotNull String getName(){
        return "Coins";
    }

    public @NotNull GetItemResult getItem(@Nullable String type, @NotNull String itemId) {
        return getItem(type, itemId, 0.0);
    }

    public @NotNull GetItemResult getItem(@Nullable String type, @NotNull String itemId, double amount) {
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final me.justeli.coins.Coins coins = (me.justeli.coins.Coins) Bukkit.getPluginManager().getPlugin("coins");
        if (coins == null){
            result.pluginIsInstalled = false;
            return result;
        }

        result.itemStack = amount != 0.0 ?
                coins.getCreateCoin().dropped(amount) :
                coins.getCreateCoin().dropped();

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
