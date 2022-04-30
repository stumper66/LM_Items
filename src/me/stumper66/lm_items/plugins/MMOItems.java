package me.stumper66.lm_items.plugins;

import me.stumper66.lm_items.GetItemResult;
import me.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MMOItems implements ItemsAPI {
    public MMOItems(){
        checkDeps();
    }

    private boolean isInstalled;
    final String[] deps = new String[] {"MMOItems", "MythicLib"};

    private void checkDeps(){
        for (final String dep : deps){
            if (!Bukkit.getPluginManager().isPluginEnabled(dep))
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull GetItemResult getItem(@Nullable final String type, @NotNull final String itemId) {
        final GetItemResult result = new GetItemResult();
        if (!isInstalled){
            result.pluginIsInstalled = false;
            return result;
        }

        result.pluginIsInstalled = true;
        final net.Indyuce.mmoitems.MMOItems mmoItems = net.Indyuce.mmoitems.MMOItems.plugin;
        result.itemStack = mmoItems.getItem(type, itemId);

        return result;
    }

    public @NotNull List<String> getItemTypes() {
        final net.Indyuce.mmoitems.MMOItems mmoItems = net.Indyuce.mmoitems.MMOItems.plugin;
        final List<String> names = new ArrayList<>(mmoItems.getTypes().getAll().size());

        for (final net.Indyuce.mmoitems.api.Type type : mmoItems.getTypes().getAll())
            names.add(type.toString());

        Collections.sort(names);

        return names;
    }
}
