package io.github.stumper66.lm_items.plugins;


import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Nexo implements ItemsAPI {

    @Override
    public boolean getIsInstalled() {
        // Simple check; you can also do a Class.forName on NexoItems if you prefer
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    @Override
    public @NotNull String getName() {
        return "Nexo";
    }

    @Override
    public @NotNull GetItemResult getItem(@NotNull ExternalItemRequest itemRequest) {
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        // Nexo: resolve ItemBuilder from an item id (e.g., "nexo:<item_id>")
        final Optional<ItemBuilder> maybeBuilder = NexoItems.optionalItemFromId(itemRequest.itemId);
        if (maybeBuilder.isEmpty()) return result;

        ItemStack stack = maybeBuilder.get().build();

        final int amount = itemRequest.amount == null ? 1 : (int) Math.round(itemRequest.amount);
        if (amount > 1) stack.setAmount(Math.min(amount, stack.getMaxStackSize()));

        result.itemStack = stack;
        return result;
    }

    @Override
    public @NotNull Collection<String> getItemTypes() {
        // Keep empty for now; LM_Items doesn’t need Nexo-specific type tokens
        return Collections.emptyList();
    }
}
