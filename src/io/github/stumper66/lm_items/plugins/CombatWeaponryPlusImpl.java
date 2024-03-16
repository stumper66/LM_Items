package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import me.helleo.cwp.CombatWeaponryPlus;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class CombatWeaponryPlusImpl implements ItemsAPI {
    private Plugin plugin;

    public boolean getIsInstalled() {
        this.plugin = Bukkit.getPluginManager().getPlugin(getName());
        return this.plugin != null;
    }

    public @NotNull String getName(){
        return "CombatWeaponryPlus";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(getIsInstalled());
        if (!result.pluginIsInstalled) return result;

        CombatWeaponryPlus cwp = (CombatWeaponryPlus) plugin;
        assert cwp != null;
        for (NamespacedKey key : cwp.keys){
            if (itemRequest.itemId.equalsIgnoreCase(key.getKey())){
                Recipe recipe = Bukkit.getRecipe(key);
                if (recipe != null)
                    result.itemStack = recipe.getResult();

                break;
            }
        }

        return result;
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }
}
