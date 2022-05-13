package io.github.stumper66.lm_items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class EventHandlers implements Listener {
    public EventHandlers(final @NotNull LM_Items main){
        this.main = main;
    }

    private final LM_Items main;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onServerLoadEvent(final ServerLoadEvent event){
        main.buildApiClasses();
    }
}
