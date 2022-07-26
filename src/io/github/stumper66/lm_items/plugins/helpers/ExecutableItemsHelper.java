package io.github.stumper66.lm_items.plugins.helpers;

import io.github.stumper66.lm_items.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class ExecutableItemsHelper {
    public static void setExtras(final @NotNull com.ssomar.executableitems.executableitems.ExecutableItem ei, final @Nullable Map<String, Object> extras){
        if (extras == null) return;

        com.ssomar.scoretestrecode.features.custom.hiders.Hiders hiders = null;

        final int usage = Utils.getIntValue(extras, "Usage", -2);

        if (usage > -2)
            ei.getUsage().setValue(Optional.of(usage));

        final int usageLimit = Utils.getIntValue(extras, "UsageLimit", -2);
        if (usageLimit > -2)
            ei.getUsageLimit().setValue(Optional.of(usageLimit));

        final int usePerDay = Utils.getIntValue(extras, "UsePerDay", -2);
        if (usageLimit > -2)
            ei.getUsePerDay().getMaxUsePerDay().setValue(Optional.of(usageLimit));

        final int durability = Utils.getIntValue(extras, "Durability", -1);
        if (durability > -1)
            ei.getDurability().setValue(Optional.of(durability));

        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideUsage"))) {
            hiders = new com.ssomar.scoretestrecode.features.custom.hiders.Hiders(ei);
            hiders.getHideUsage().setValue(true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideEnchantments"))) {
            if (hiders == null) hiders = new com.ssomar.scoretestrecode.features.custom.hiders.Hiders(ei);
            hiders.getHideEnchantments().setValue(true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideAttributes"))) {
            if (hiders == null) hiders = new com.ssomar.scoretestrecode.features.custom.hiders.Hiders(ei);
            hiders.getHideAttributes().setValue(true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HideUnbreakable"))) {
            if (hiders == null) hiders = new com.ssomar.scoretestrecode.features.custom.hiders.Hiders(ei);
            hiders.getHideUnbreakable().setValue(true);
        }
        if ("true".equalsIgnoreCase(Utils.getValueString(extras, "HidePotionEffects"))) {
            if (hiders == null) hiders = new com.ssomar.scoretestrecode.features.custom.hiders.Hiders(ei);
            hiders.getHidePotionEffects().setValue(true);
        }

        if (hiders != null)
            ei.setHiders(hiders);
    }
}
