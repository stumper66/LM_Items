package io.github.stumper66.lm_items.plugins;


import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.SupportsExtras;
import io.github.stumper66.lm_items.Utils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemsAdder implements ItemsAPI, SupportsExtras {
    public boolean getIsInstalled() {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    public @NotNull String getName(){
        return "ItemsAdder";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final List<dev.lone.itemsadder.api.CustomStack> items =
                dev.lone.itemsadder.api.ItemsAdder.getAllItems(itemRequest.itemId);
        if (items == null || items.isEmpty())
            return result;

        final int amount = itemRequest.amount == null ?
                1 :
                (int)Math.round(itemRequest.amount);

        final dev.lone.itemsadder.api.CustomStack firstItem = items.get(0);
        setExtras(firstItem, itemRequest.extras);
        result.itemStack = firstItem.getItemStack();
        if (amount > 1)
            result.itemStack.setAmount(amount);

        return result;
    }

    private void setExtras(final @NotNull dev.lone.itemsadder.api.CustomStack customStack, final @Nullable Map<String, Object> extras){
        if (extras == null) return;

        final int usage = Utils.getIntValue(extras, "Usages", -2);
        if (usage > -2)
            customStack.setUsages(usage);

        final int durability = Utils.getIntValue(extras, "Durability", -1);
        if (durability > -1)
            customStack.setDurability(durability);
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }

    public @NotNull Collection<String> getSupportedExtras(){
        return List.of("Usages", "Durability");
    }
}
