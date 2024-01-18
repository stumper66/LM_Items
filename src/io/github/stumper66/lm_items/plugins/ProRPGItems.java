package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ProRPGItems implements ItemsAPI {
    public ProRPGItems(){
        checkDeps();
    }

    private boolean isInstalled;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "ProMCCore")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName() {
        return "ProRPGItems";
    }

    public @NotNull GetItemResult getItem(@NotNull ExternalItemRequest itemRequest) {
        return new GetItemResult();
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
