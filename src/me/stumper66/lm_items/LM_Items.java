package me.stumper66.lm_items;

import me.stumper66.lm_items.plugins.MMOItems;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LM_Items extends JavaPlugin implements LM_Items_API {
    public LM_Items(){
        plugin = this;
        this.supportedPlugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.supportedPluginNames = List.of("MMOItems");
    }

    public static LM_Items plugin;
    final Map<String, ItemsAPI> supportedPlugins;
    final List<String> supportedPluginNames;

    @Override
    public void onEnable() {
        for (int i = 0; i < this.supportedPluginNames.size(); i++) {
            switch (i){
                case 0:
                    this.supportedPlugins.put(this.supportedPluginNames.get(i), new MMOItems());
                break;
            }
        }

        final PluginCommand cmd = getCommand("lm_items");
        if (cmd == null)
            Utils.logger.warning("Command &b/lm_items&7 is unavailable, is it not registered in plugin.yml?");
        else
            cmd.setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {

    }

    public boolean doesSupportPlugin(@NotNull final String pluginName){
        return this.supportedPlugins.containsKey(pluginName);
    }

    public @Nullable ItemsAPI getItemAPIForPlugin(final @NotNull String pluginName) {
        return this.supportedPlugins.get(pluginName);
    }
}
