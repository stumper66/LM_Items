package io.github.stumper66.lm_items;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Commands implements CommandExecutor, TabCompleter {
    public Commands(final @NotNull LM_Items main){
        this.main = main;
    }

    private final LM_Items main;
    private CommandSender sender;
    private String[] args;

    public boolean onCommand(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String label, final String @NotNull [] args) {
        this.sender = sender;
        this.args = args;
        if (!hasPermissions(null)) return true;

        if (args.length == 0){
            showSyntax();
            return true;
        }

        switch (args[0].toLowerCase()){
            case "info":
                showInfo();
                break;
            case "installed_plugins":
                showInstalledPlugins();
                break;
            case "supported_plugins":
                showSupportedPlugins();
                break;
            case "types":
                showTypesOrExtras(false);
                break;
            case "supported_extras":
                showTypesOrExtras(true);
                break;
            default:
                showSyntax();
                break;
        }

        return true;
    }

    private boolean hasPermissions(final @Nullable String perm){
        if (sender.hasPermission("lm_items" + (perm == null ? "" : "." + perm)))
            return true;
        else {
            sender.sendMessage("You don't have permissions for this command");
            return false;
        }
    }

    public void showSyntax() {
        sender.sendMessage("Usage: info | installed_plugins | supported_extras | supported_plugins | types");
    }

    private void showInfo(){
        if (!hasPermissions("info")) return;

        sender.sendMessage("LM_Items, version " + main.getDescription().getVersion() + System.lineSeparator() +
                "Created by stumper66, for use with LevelledMobs");
    }

    private void showTypesOrExtras(final boolean isExtras){
        if (isExtras && !hasPermissions("supported_extras")) return;
        else if (!isExtras && !hasPermissions("types")) return;

        if (args.length == 1){
            sender.sendMessage("Must specify plugin name");
            return;
        }

        final String pluginName = args[1];
        if (!main.supportedPlugins.containsKey(pluginName)){
            sender.sendMessage(String.format("'%s' is not a plugin supported by LM_Items", pluginName));
            return;
        }

        final ItemsAPI itemsAPI = main.supportedPlugins.get(pluginName);
        if (!itemsAPI.getIsInstalled()){
            sender.sendMessage(String.format("'%s' is currently not installed", pluginName));
            return;
        }

        final StringBuilder sb = new StringBuilder();

        if (isExtras){
            if (itemsAPI instanceof SupportsExtras){
                final SupportsExtras supportsExtras = (SupportsExtras) itemsAPI;
                for (final String name : supportsExtras.getSupportedExtras()){
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(name);
                }
            }
            else {
                sender.sendMessage("This plugin doesn't have any supported extras");
                return;
            }
        }
        else {
            final Collection<String> types = itemsAPI.getItemTypes();
            if (types.isEmpty()) {
                sender.sendMessage("There are no types for this plugin known by LM_Items");
                return;
            }

            for (final String typeName : types){
                if (sb.length() > 0) sb.append(", ");
                sb.append(typeName);
            }
        }

        sender.sendMessage(sb.toString());
    }

    private void showInstalledPlugins(){
        if (!hasPermissions("installed_plugins")) return;

        final StringBuilder sb = new StringBuilder();
        for (final String plugin : main.supportedPlugins.keySet()){
            final ItemsAPI itemsAPI = main.supportedPlugins.get(plugin);
            if (!itemsAPI.getIsInstalled()) continue;

            if (sb.length() > 0) sb.append(", ");
            sb.append(plugin);
        }

        if (sb.length() > 0)
            sender.sendMessage("Installed plugins: " + sb);
        else
            sender.sendMessage("There are no installed plugins that are supported");
    }
    private void showSupportedPlugins(){
        if (!hasPermissions("supported_plugins")) return;

        final StringBuilder sb = new StringBuilder();

        for (final String pluginName : main.supportedPlugins.keySet()){
            if (sb.length() > 0) sb.append(", ");
            sb.append(pluginName);
        }

        sender.sendMessage("Supported plugin: " + sb);
    }

    @Override
    public @Nullable List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String @NotNull [] args){
        if (args.length <= 1)
            return List.of("info", "installed_plugins", "supported_plugins", "types", "supported_extras");

        if (args.length == 2 && "types".equalsIgnoreCase(args[0]) && sender.hasPermission("lm_items.types")){
            final List<String> plugins = new LinkedList<>();
            for (final String plugin : main.supportedPlugins.keySet()){
                final ItemsAPI itemsAPI = main.supportedPlugins.get(plugin);
                if (itemsAPI.getIsInstalled())
                    plugins.add(itemsAPI.getName());
            }

            Collections.sort(plugins);
            return plugins;
        }

        return Collections.emptyList();
    }
}
