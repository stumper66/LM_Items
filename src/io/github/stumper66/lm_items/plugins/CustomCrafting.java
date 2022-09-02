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
public class CustomCrafting implements ItemsAPI {
    public CustomCrafting() {
        checkDeps();
    }

    private boolean isInstalled;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "WolfyUtilities")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "CustomCrafting";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        int useAmount = 1;
        if (itemRequest.amount != null && result.itemStack != null) {
            useAmount = (int)(double)itemRequest.amount;
        }

        // since we're using namespaced keys we'll get an exception if there's a space in it
        final String itemId = itemRequest.itemId.replace(" ", "_");

        final me.wolfyscript.utilities.api.WolfyUtilities api = me.wolfyscript.customcrafting.CustomCrafting.inst().getApi();
        final me.wolfyscript.utilities.util.NamespacedKey key = new me.wolfyscript.utilities.util.NamespacedKey("customcrafting", itemId);
        final me.wolfyscript.utilities.api.inventory.custom_items.CustomItem customItem = api.getRegistries().getCustomItems().get(key);

        if (customItem != null) {
            result.itemStack = customItem.create(useAmount);
            if (result.itemStack.getAmount() > result.itemStack.getMaxStackSize())
                result.itemStack.setAmount(result.itemStack.getMaxStackSize());
        }

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
