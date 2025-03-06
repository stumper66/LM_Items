package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

@SuppressWarnings("unused")
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

        final int amount = itemRequest.amount == null ?
                1 :
                (int) Math.round(itemRequest.amount);

        result.itemStacks = new LinkedList<>();
        final BukkitItemStack bukkitItemStack = (BukkitItemStack) item.get().generateItemStack(amount);
        result.itemStacks.add(bukkitItemStack.getItemStack());

        if (!result.itemStacks.isEmpty())
            result.itemStack = result.itemStacks.iterator().next();

        return result;
    }

    @Override
    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
