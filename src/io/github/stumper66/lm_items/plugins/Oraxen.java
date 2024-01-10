package io.github.stumper66.lm_items.plugins;

import io.th0rgal.oraxen.api.OraxenItems;
import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class Oraxen implements ItemsAPI{
    @Override
    public boolean getIsInstalled()  {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    @Override
    public @NotNull String getName() {
        return "Oraxen";
    }

    @Override
    public @NotNull GetItemResult getItem(@NotNull ExternalItemRequest itemRequest) {
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final ItemBuilder stack = OraxenItems.getItemById(itemRequest.itemId);

        if (stack == null) return result;

        final int amount = itemRequest.amount == null ?
                1 :
                (int) Math.round(itemRequest.amount);

        result.itemStack = stack.build();
        if (amount > 1)
            result.itemStack.setAmount(amount);
        return result;
    }

    @Override
    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}