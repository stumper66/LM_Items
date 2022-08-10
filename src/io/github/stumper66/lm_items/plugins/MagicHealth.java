package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unused")
public class MagicHealth implements ItemsAPI {
    public MagicHealth(){
        this.itemMappings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    private final Map<String, ItemStack> itemMappings;

    public boolean getIsInstalled() {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    public @NotNull String getName(){
        return "MagicHealth";
    }

    private void buildMappings(){
        for(ItemStack item : net.portalsam.magichealth.item.MagicHealthItems.MAGIC_HEALTH_ITEMS) {
            final String displayName = Utils.getDisplayName(item);
            itemMappings.put(displayName, item);
        }
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        if (itemMappings.isEmpty()) buildMappings();

        final ItemStack item = itemMappings.get(itemRequest.itemId);

        if (item != null){
            result.itemStack = item.clone();
            int useAmount = (int)(itemRequest.amount != null ? itemRequest.amount : 1.0);
            if (useAmount > 1)
                result.itemStack.setAmount(useAmount);
        }

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
