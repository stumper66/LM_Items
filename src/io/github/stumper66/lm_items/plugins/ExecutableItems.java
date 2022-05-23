package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class ExecutableItems implements ItemsAPI {
    public ExecutableItems() {
        checkDeps();
    }

    private boolean isInstalled;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "SCore")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "ExecutableItems";
    }

    public @NotNull GetItemResult getItem(@Nullable final String type, @NotNull final String itemId) {
        return getItem(type, itemId, 1.0);
    }

    public @NotNull GetItemResult getItem(final @Nullable String type, final @NotNull String itemId, final double amount){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        final Optional<com.ssomar.executableitems.executableitems.ExecutableItem> oOpt =
                com.ssomar.executableitems.executableitems.ExecutableItemsManager.getInstance().getLoadedObjectWithID(itemId);
        oOpt.ifPresent(executableItem -> result.itemStack = executableItem.buildItem(1, null, -147));

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
