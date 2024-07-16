package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class ExcellentCrates implements ItemsAPI {
    public ExcellentCrates() {
        checkDeps();
    }

    private boolean isInstalled;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "NexEngine")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "ExcellentCrates";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        int useAmount = 1;
        if (itemRequest.amount != null && result.itemStack != null) {
            useAmount = (int)(double)itemRequest.amount;
        }

        if (itemRequest.itemType == null || itemRequest.itemType.isEmpty() || "key".equalsIgnoreCase(itemRequest.itemType))
        {
            final su.nightexpress.excellentcrates.key.CrateKey key =
                    su.nightexpress.excellentcrates.ExcellentCratesAPI.getKeyManager().getKeyById(itemRequest.itemId);

            if (key != null) {
                result.itemStack = key.getItem();
                if (useAmount > 1)
                    result.itemStack.setAmount(Math.min(useAmount, result.itemStack.getMaxStackSize()));
            }
        }
        else if ("crate".equalsIgnoreCase(itemRequest.itemType)){
            final su.nightexpress.excellentcrates.crate.impl.Crate crate =
                    su.nightexpress.excellentcrates.ExcellentCratesAPI.getCrateManager().getCrateById(itemRequest.itemId);

            if (crate != null) {
                crate.onSave();
                result.itemStack = crate.getItem();

                if (useAmount > 1)
                    result.itemStack.setAmount(Math.min(useAmount, result.itemStack.getMaxStackSize()));
            }
        }
        else {
            result.typeIsNotSupported = true;
        }

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return List.of("crate", "key");
    }
}
