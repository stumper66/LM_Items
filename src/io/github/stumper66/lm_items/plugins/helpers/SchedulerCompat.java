package io.github.stumper66.lm_items.plugins.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Minimal Folia/Paper/Spigot scheduler bridge. */
public final class SchedulerCompat {
    private SchedulerCompat() {}

    public static boolean isFolia() {
        // Folia servers expose getGlobalRegionScheduler()
        try {
            Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /* ---------------- Handle ---------------- */

    /** Minimal cancel handle that works across implementations. */
    public interface CancelHandle {
        void cancel();
        boolean isCancelled();
    }

    private static Duration ticksToDuration(long ticks) {
        if (ticks < 0) ticks = 0;
        return Duration.ofMillis(ticks * 50L);
    }

    /* ---------------- Global (main-thread) scheduling ---------------- */

    /** Run next tick (or ASAP) on the main thread. */
    static CancelHandle runNow(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);
        if (isFolia()) {
            final var ref = new AtomicReference<io.papermc.paper.threadedregions.scheduler.ScheduledTask>();
            var t = Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run());
            ref.set(t);
            return new CancelHandle() {
                public void cancel() { var s = ref.getAndSet(null); if (s != null) s.cancel(); }
                public boolean isCancelled() { return ref.get() == null || ref.get().isCancelled(); }
            };
        } else {
            BukkitTask t = Bukkit.getScheduler().runTask(plugin, task);
            return bukkitHandle(t);
        }
    }

    /**
     * Run after a delay (ticks) on the main thread.
     */
    public static void runLater(Plugin plugin, Runnable task, long delayTicks) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);
        if (isFolia()) {
            final var ref = new AtomicReference<io.papermc.paper.threadedregions.scheduler.ScheduledTask>();
            var t = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), delayTicks);
            ref.set(t);
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskLater(plugin, task, Math.max(0L, delayTicks));
            bukkitHandle(t);
        }
    }

    /** Run repeating on the main thread. */
    static CancelHandle runRepeating(Plugin plugin, Runnable task, long initialDelayTicks, long periodTicks) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);
        if (periodTicks <= 0) periodTicks = 1;
        if (isFolia()) {
            final var ref = new AtomicReference<io.papermc.paper.threadedregions.scheduler.ScheduledTask>();
            var t = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin, st -> task.run(), initialDelayTicks, periodTicks);
            ref.set(t);
            return new CancelHandle() {
                public void cancel() { var s = ref.getAndSet(null); if (s != null) s.cancel(); }
                public boolean isCancelled() { return ref.get() == null || ref.get().isCancelled(); }
            };
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskTimer(plugin, task,
                    Math.max(0L, initialDelayTicks), Math.max(1L, periodTicks));
            return bukkitHandle(t);
        }
    }

    /* ---------------- Region / Player (Folia main-thread safe) ---------------- */

    /** Run at a location’s region next tick. On non-Folia, this falls back to normal main thread. */
    static CancelHandle runAtLocation(Plugin plugin, Location where, Runnable task) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(where);
        Objects.requireNonNull(task);
        if (isFolia()) {
            final var ref = new AtomicReference<io.papermc.paper.threadedregions.scheduler.ScheduledTask>();
            var t = Bukkit.getRegionScheduler().run(plugin, where, st -> task.run());
            ref.set(t);
            return new CancelHandle() {
                public void cancel() { var s = ref.getAndSet(null); if (s != null) s.cancel(); }
                public boolean isCancelled() { return ref.get() == null || ref.get().isCancelled(); }
            };
        } else {
            BukkitTask t = Bukkit.getScheduler().runTask(plugin, task);
            return bukkitHandle(t);
        }
    }

    /** Run repeating in the player’s region. On non-Folia, normal repeating main-thread. */
    static CancelHandle runPlayerRepeating(Plugin plugin, Player player, Runnable task,
                                           long initialDelayTicks, long periodTicks) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(player);
        Objects.requireNonNull(task);
        if (isFolia()) {
            final var ref = new AtomicReference<io.papermc.paper.threadedregions.scheduler.ScheduledTask>();
            var t = player.getScheduler().runAtFixedRate(
                    plugin, st -> task.run(), () -> {}, initialDelayTicks, periodTicks);
            ref.set(t);
            return new CancelHandle() {
                public void cancel() { var s = ref.getAndSet(null); if (s != null) s.cancel(); }
                public boolean isCancelled() { return ref.get() == null || ref.get().isCancelled(); }
            };
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskTimer(plugin, task,
                    Math.max(0L, initialDelayTicks), Math.max(1L, periodTicks));
            return bukkitHandle(t);
        }
    }

    /* ---------------- Async scheduling (compat: Duration or TimeUnit) ---------------- */

    @SuppressWarnings("unchecked")
    static CancelHandle runAsync(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);

        if (isFolia()) {
            try {
                // Prefer: AsyncScheduler.runNow(Plugin, Consumer<ScheduledTask>)
                var m = Bukkit.getAsyncScheduler().getClass()
                        .getMethod("runNow", Plugin.class, java.util.function.Consumer.class);
                final var ref = new AtomicReference<Object>();
                Object schedTask = m.invoke(Bukkit.getAsyncScheduler(), plugin,
                        (java.util.function.Consumer<Object>) st -> task.run());
                ref.set(schedTask);
                return new CancelHandle() {
                    public void cancel() { var s = ref.getAndSet(null); if (s != null) invokeCancel(s); }
                    public boolean isCancelled() { var s = ref.get(); return s == null || SchedulerCompat.isCancelled(s); }
                };
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("AsyncScheduler.runNow not found", e);
            }
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return bukkitHandle(t);
        }
    }

    @SuppressWarnings("unchecked")
    static CancelHandle runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);

        if (isFolia()) {
            final Object scheduler = Bukkit.getAsyncScheduler();
            final Class<?> cls = scheduler.getClass();
            try {
                // Try new API: runDelayed(Plugin, Consumer, Duration)
                var m = cls.getMethod("runDelayed", Plugin.class, java.util.function.Consumer.class, java.time.Duration.class);
                final var ref = new AtomicReference<Object>();
                Object st = m.invoke(scheduler, plugin,
                        (java.util.function.Consumer<Object>) ignored -> task.run(),
                        java.time.Duration.ofMillis(Math.max(0L, delayTicks) * 50L));
                ref.set(st);
                return new CancelHandle() {
                    public void cancel() { var s = ref.getAndSet(null); if (s != null) invokeCancel(s); }
                    public boolean isCancelled() { var s = ref.get(); return s == null || SchedulerCompat.isCancelled(s); }
                };
            } catch (NoSuchMethodException e) {
                try {
                    // Old API: runDelayed(Plugin, Consumer, long, TimeUnit)
                    var m = cls.getMethod("runDelayed", Plugin.class, java.util.function.Consumer.class, long.class, java.util.concurrent.TimeUnit.class);
                    final var ref = new AtomicReference<Object>();
                    Object st = m.invoke(scheduler, plugin,
                            (java.util.function.Consumer<Object>) ignored -> task.run(),
                            Math.max(0L, delayTicks), java.util.concurrent.TimeUnit.MILLISECONDS /* will be scaled below */);
                    // Old API expects ticks or time unit? Older Paper used TimeUnit; convert ticks->MILLIS above
                    ref.set(st);
                    return new CancelHandle() {
                        public void cancel() { var s = ref.getAndSet(null); if (s != null) invokeCancel(s); }
                        public boolean isCancelled() { var s = ref.get(); return s == null || SchedulerCompat.isCancelled(s); }
                    };
                } catch (ReflectiveOperationException e2) {
                    throw new IllegalStateException("AsyncScheduler.runDelayed not found (Duration or TimeUnit)", e2);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("AsyncScheduler.runDelayed invocation failed", e);
            }
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, Math.max(0L, delayTicks));
            return bukkitHandle(t);
        }
    }

    @SuppressWarnings("unchecked")
    static CancelHandle runAsyncRepeating(Plugin plugin, Runnable task, long initialDelayTicks, long periodTicks) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(task);
        if (periodTicks <= 0) periodTicks = 1;

        if (isFolia()) {
            final Object scheduler = Bukkit.getAsyncScheduler();
            final Class<?> cls = scheduler.getClass();
            try {
                // Try new API: runAtFixedRate(Plugin, Consumer, Duration initial, Duration period)
                var m = cls.getMethod("runAtFixedRate", Plugin.class, java.util.function.Consumer.class,
                        java.time.Duration.class, java.time.Duration.class);
                final var ref = new AtomicReference<Object>();
                Object st = m.invoke(scheduler, plugin,
                        (java.util.function.Consumer<Object>) ignored -> task.run(),
                        java.time.Duration.ofMillis(Math.max(0L, initialDelayTicks) * 50L),
                        java.time.Duration.ofMillis(Math.max(1L, periodTicks) * 50L));
                ref.set(st);
                return new CancelHandle() {
                    public void cancel() { var s = ref.getAndSet(null); if (s != null) invokeCancel(s); }
                    public boolean isCancelled() { var s = ref.get(); return s == null || SchedulerCompat.isCancelled(s); }
                };
            } catch (NoSuchMethodException e) {
                try {
                    // Old API: runAtFixedRate(Plugin, Consumer, long delay, long period, TimeUnit unit)
                    var m = cls.getMethod("runAtFixedRate", Plugin.class, java.util.function.Consumer.class,
                            long.class, long.class, java.util.concurrent.TimeUnit.class);
                    final var ref = new AtomicReference<Object>();
                    // Convert ticks -> milliseconds (50 ms per tick) and use TimeUnit.MILLISECONDS
                    long delayMs  = Math.max(0L, initialDelayTicks) * 50L;
                    long periodMs = Math.max(1L, periodTicks) * 50L;
                    Object st = m.invoke(scheduler, plugin,
                            (java.util.function.Consumer<Object>) ignored -> task.run(),
                            delayMs, periodMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                    ref.set(st);
                    return new CancelHandle() {
                        public void cancel() { var s = ref.getAndSet(null); if (s != null) invokeCancel(s); }
                        public boolean isCancelled() { var s = ref.get(); return s == null || SchedulerCompat.isCancelled(s); }
                    };
                } catch (ReflectiveOperationException e2) {
                    throw new IllegalStateException("AsyncScheduler.runAtFixedRate not found (Duration or TimeUnit)", e2);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("AsyncScheduler.runAtFixedRate invocation failed", e);
            }
        } else {
            BukkitTask t = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin, task, Math.max(0L, initialDelayTicks), Math.max(1L, periodTicks));
            return bukkitHandle(t);
        }
    }

    /* ---- small reflection helpers to cancel/check Folia ScheduledTask ---- */

    private static void invokeCancel(Object scheduledTask) {
        try { scheduledTask.getClass().getMethod("cancel").invoke(scheduledTask); }
        catch (ReflectiveOperationException ignored) {}
    }
    private static boolean isCancelled(Object scheduledTask) {
        try { return (boolean) scheduledTask.getClass().getMethod("isCancelled").invoke(scheduledTask); }
        catch (ReflectiveOperationException e) { return false; }
    }


    /* ---------------- Internals ---------------- */

    private static CancelHandle bukkitHandle(BukkitTask t) {
        final AtomicReference<BukkitTask> ref = new AtomicReference<>(t);
        return new CancelHandle() {
            public void cancel() { var cur = ref.getAndSet(null); if (cur != null) cur.cancel(); }
            public boolean isCancelled() { var cur = ref.get(); return cur == null || cur.isCancelled(); }
        };
    }
}
