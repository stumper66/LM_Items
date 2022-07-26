package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.SupportsExtras;
import io.github.stumper66.lm_items.plugins.helpers.ExecutableItemsHelper;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class ExecutableItems implements ItemsAPI, SupportsExtras {
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

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        final Optional<com.ssomar.executableitems.executableitems.ExecutableItem> oOpt =
                com.ssomar.executableitems.executableitems.manager.ExecutableItemsManager.getInstance()
                        .getLoadedObjectWithID(itemRequest.itemId);

        oOpt.ifPresent(executableItem -> {
            ExecutableItemsHelper.setExtras(executableItem, itemRequest.extras);
            final Optional<Integer> usageOpt = Optional.of(-147);
            final Optional<Player> playerOpt = Optional.empty();
            double useAmount = itemRequest.amount != null ? itemRequest.amount : 1.0;
            result.itemStack = executableItem.buildItem((int)useAmount, usageOpt, playerOpt);
        });

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }

    public @NotNull Collection<String> getSupportedExtras(){
        return List.of("Usage", "UsageLimit", "UsePerDay", "Durability", "HideUsage",
                "HideEnchantments", "HideAttributes", "HideUnbreakable", "HidePotionEffects");
    }
}
