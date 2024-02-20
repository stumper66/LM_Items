package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unused", "deprecation"})
public class MythicMobs implements ItemsAPI {
    @Override
    public boolean getIsInstalled()  {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    @Override
    public @NotNull String getName() {
        return "MythicMobs";
    }

    @Override
    public @NotNull GetItemResult getItem(@NotNull ExternalItemRequest itemRequest) {
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        final MythicBukkit mb = (MythicBukkit) Bukkit.getPluginManager().getPlugin("MythicMobs");
        assert mb != null;
        final Optional<MythicItem> item = mb.getItemManager().getItem(itemRequest.itemId);
        if (item.isEmpty()) {
            return result;
        }

        if (item.get().getCachedMenuItem() == null)
            item.get().buildItemCache();
        result.itemStack = item.get().getCachedMenuItem();

        if (result.itemStack == null) return result;

        final List<String> lore = result.itemStack.getLore();

        if (lore != null) {
            boolean madeChanges = false;
            for (int i = lore.size() - 1; i >= 0; i--) {
                if (lore.get(i).contains("Left-Click to get Item") ||
                        lore.get(i).contains("Right-Click to Edit")) {
                    lore.remove(i);
                    madeChanges = true;
                }
            }

            if (madeChanges) result.itemStack.setLore(lore);
        }

        final int amount = itemRequest.amount == null ?
                1 :
                (int) Math.round(itemRequest.amount);

        if (amount > 1)
            result.itemStack.setAmount(amount);
        return result;
    }

    @Override
    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
