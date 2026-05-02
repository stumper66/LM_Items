package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import me.justeli.coins.item.CoinMeta;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

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
        if (coins == null) return result;

        if (itemRequest.amount != null && itemRequest.amount > 0d) {
            result.itemStack = coins.getCreateCoin().createOther()
                    .setData(CoinMeta.COINS_WORTH, itemRequest.amount)
                    .build();
        }
        else
            result.itemStack = coins.getCreateCoin().createDropped();

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return List.of("coin");
    }

    public @NotNull Collection<String> getSupportedExtras() {
        return List.of("amount");
    }
}
