package me.stumper66.lm_items;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Commands implements CommandExecutor, TabCompleter {
    public Commands(final @NotNull LM_Items main){
        this.main = main;
    }

    private final LM_Items main;
    private CommandSender sender;

    public boolean onCommand(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String label, final String @NotNull [] args) {
        this.sender = sender;
        Utils.logger.info("args len: " + args.length);
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
        sender.sendMessage("Usage: info | installed_plugins | supported_plugins");
    }

    private void showInfo(){
        if (!hasPermissions("info")) return;

        sender.sendMessage("LM_Items, version " + main.getDescription().getVersion() + System.lineSeparator() +
                "Created by stumper66, for use with LevelledMobs");
    }

    private void showInstalledPlugins(){
        final StringBuilder sb = new StringBuilder();
        for (final String plugin : main.supportedPlugins.keySet()){
            final ItemsAPI itemsAPI = main.supportedPlugins.get(plugin);

            if (sb.length() > 0) sb.append(", ");
            if (itemsAPI.getIsInstalled()) sb.append(plugin);
        }

        if (sb.length() > 0)
            sender.sendMessage("Installed plugins: " + sb);
        else
            sender.sendMessage("There are now installed plugins that are supported");
    }
    private void showSupportedPlugins(){
        final StringBuilder sb = new StringBuilder();

        for (final String pluginName : main.supportedPluginNames){
            if (sb.length() > 0) sb.append(", ");
            sb.append(pluginName);
        }

        sender.sendMessage("Supported plugin: " + sb);
    }

    @Override
    public @Nullable List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String @NotNull [] args){
        if (args.length <= 1)
            return List.of("info", "installed_plugins", "supported_plugins");

        return Collections.emptyList();
    }
}
