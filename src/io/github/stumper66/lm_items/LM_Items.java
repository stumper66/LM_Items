package io.github.stumper66.lm_items;

import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        final long startedTime = System.currentTimeMillis();

        buildApiClasses();
        registerCommands();

        Utils.logger.info(String.format("LM_Items start-up complete, took %s ms",
                (System.currentTimeMillis() - startedTime)));
    }

    private void buildApiClasses(){
        final List<String> names = List.of("Coins", "MMOItems", "ExecutableItems");

        for (final String name : names) {
            ItemsAPI api = null;
            try {
                final Class<?> clazz = Class.forName("io.github.stumper66.lm_items.plugins." + name);
                api = (ItemsAPI) clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
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
            Utils.logger.warning("Command &b/lm_items&7 is unavailable, is it not registered in plugin.yml?");
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
