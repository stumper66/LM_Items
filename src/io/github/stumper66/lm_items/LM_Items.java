package io.github.stumper66.lm_items;

import io.github.stumper66.lm_items.plugins.Coins;
import io.github.stumper66.lm_items.plugins.CombatWeaponryPlusImpl;
import io.github.stumper66.lm_items.plugins.CustomCrafting;
import io.github.stumper66.lm_items.plugins.CustomHeads;
import io.github.stumper66.lm_items.plugins.CustomItems;
import io.github.stumper66.lm_items.plugins.EcoAPI;
import io.github.stumper66.lm_items.plugins.ExcellentCrates;
import io.github.stumper66.lm_items.plugins.ExecutableItems;
import io.github.stumper66.lm_items.plugins.HeadDatabase;
import io.github.stumper66.lm_items.plugins.ItemEdit;
import io.github.stumper66.lm_items.plugins.ItemsAdder;
import io.github.stumper66.lm_items.plugins.MMOItems;
import io.github.stumper66.lm_items.plugins.MagicHealth;
import io.github.stumper66.lm_items.plugins.MythicMobs;
import io.github.stumper66.lm_items.plugins.Nexo;
import io.github.stumper66.lm_items.plugins.Oraxen;
import io.github.stumper66.lm_items.plugins.ecosupported.EcoArmor;
import io.github.stumper66.lm_items.plugins.ecosupported.EcoCrates;
import io.github.stumper66.lm_items.plugins.ecosupported.EcoItems;
import io.github.stumper66.lm_items.plugins.ecosupported.Reforges;
import io.github.stumper66.lm_items.plugins.ecosupported.Talismans;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LM_Items extends JavaPlugin implements LM_Items_API {
    public LM_Items(){
        plugin = this;
        this.supportedPlugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public static LM_Items plugin;
    final Map<String, ItemsAPI> supportedPlugins;
    private volatile boolean classesAreBuilding;
    private boolean doneLoadingClasses;

    @Override
    public void onEnable() {
        final long startedTime = System.currentTimeMillis();

        registerCommands();
        buildApiClasses();

        Utils.logger.info(String.format("LM_Items: start-up complete, took %s ms",
                (System.currentTimeMillis() - startedTime)));
    }

    public void buildApiClasses(){
        if (classesAreBuilding || doneLoadingClasses) return;

        classesAreBuilding = true;
        Utils.logger.info("LM_Items: Building API classes");

        final List<Class<?>> classes = List.of(
                Coins.class,
                MMOItems.class,
                ExecutableItems.class,
                EcoAPI.class,
                CustomItems.class,
                ItemsAdder.class,
                MagicHealth.class,
                CustomCrafting.class,
                ExcellentCrates.class,
                HeadDatabase.class,
                ItemEdit.class,
                Oraxen.class,
                Nexo.class,
                MythicMobs.class,
                CombatWeaponryPlusImpl.class,
                CustomHeads.class
        );

        for (final Class<?> clazz : classes) {
            ItemsAPI api = null;
            try {
                api = (ItemsAPI) clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (api != null) {
                this.supportedPlugins.put(api.getName(), api);
                if (api.getIsInstalled())
                    Utils.logger.info("LM_Items: Discovered " + api.getName());
            }

            doneLoadingClasses = true;
            classesAreBuilding = false;
        }

        if (!this.supportedPlugins.containsKey("eco") || !this.supportedPlugins.get("eco").getIsInstalled())
            return;

        // eco plugin is required for all of the following:
        final List<Class<?>> ecoSupported = List.of(
                EcoArmor.class, EcoCrates.class, EcoItems.class, Reforges.class, Talismans.class
        );

        for (final Class<?> clazz : ecoSupported) {
            ItemsAPI api = null;
            try {
                api = (ItemsAPI) clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (api != null)
                this.supportedPlugins.put(api.getName(), api);
        }
    }

    private void registerCommands(){
        final PluginCommand cmd = getCommand("lm_items");
        if (cmd == null)
            Utils.logger.warning("LM_Items: Command &b/lm_items&7 is unavailable, is it not registered in plugin.yml?");
        else
            cmd.setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {
        this.supportedPlugins.clear();
    }

    public boolean doesSupportPlugin(@NotNull final String pluginName){
        return this.supportedPlugins.containsKey(pluginName);
    }

    public @Nullable ItemsAPI getItemAPIForPlugin(final @NotNull String pluginName) {
        return this.supportedPlugins.get(pluginName);
    }
}
