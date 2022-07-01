package io.github.stumper66.lm_items.plugins;

import com.ssomar.executableitems.executableitems.hiders.HiderEnum;
import com.ssomar.executableitems.executableitems.hiders.Hiders;
import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.SupportsExtras;
import io.github.stumper66.lm_items.Utils;
import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
                com.ssomar.executableitems.executableitems.ExecutableItemsManager.getInstance().getLoadedObjectWithID(itemRequest.itemId);
        oOpt.ifPresent(executableItem -> {
            setExtras(executableItem, itemRequest.extras);
            double useAmount = itemRequest.amount != null ? itemRequest.amount : 1.0;
            result.itemStack = executableItem.buildItem((int)useAmount, null, -147);
        });

        return result;
    }

    private void setExtras(final @NotNull com.ssomar.executableitems.executableitems.ExecutableItem ei, final @Nullable Map<String, Object> extras){
        if (extras == null) return;

        Hiders hiders = null;

        final int usage = Utils.getIntValue(extras, "Usage", -2);
        if (usage > -2)
            ei.setUse(usage);

        final int usageLimit = Utils.getIntValue(extras, "UsageLimit", -2);
        if (usageLimit > -2)
            ei.setUsageLimit(usageLimit);

        final int usePerDay = Utils.getIntValue(extras, "UsePerDay", -2);
        if (usageLimit > -2)
            ei.setUsePerDay(usePerDay);

        final int durability = Utils.getIntValue(extras, "Durability", -1);
        if (durability > -1)
            ei.setDurability(durability);

        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideUsage"))) {
            hiders = new Hiders();
            hiders.set(HiderEnum.HIDE_USAGE, true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideEnchantments"))) {
            if (hiders == null) hiders = new Hiders();
            hiders.set(HiderEnum.HIDE_ENCHANTMENTS, true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideAttributes"))) {
            if (hiders == null) hiders = new Hiders();
            hiders.set(HiderEnum.HIDE_ATTRIBUTES, true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideUnbreakable"))) {
            if (hiders == null) hiders = new Hiders();
            hiders.set(HiderEnum.HIDE_UNBREAKABLE, true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HidePotionEffects"))) {
            if (hiders == null) hiders = new Hiders();
            hiders.set(HiderEnum.HIDE_POTIONS, true);
        }

        if (hiders != null)
            ei.setHiders(hiders);
    }

    public @NotNull Collection<String> getItemTypes() {
        return Collections.emptyList();
    }

    public @NotNull Collection<String> getSupportedExtras(){
        return List.of("Usage", "UsageLimit", "UsePerDay", "Durability", "HideUsage",
                "HideEnchantments", "HideAttributes", "HideUnbreakable", "HidePotionEffects");
    }
}
