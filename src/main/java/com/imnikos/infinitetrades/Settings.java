package com.imnikos.infinitetrades;

import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;

/** Immutable snapshot of config.yml. Swapped atomically on reload. */
public record Settings(
        boolean infiniteStock,
        boolean removeDemandPricing,
        boolean includeWanderingTraders,
        Set<String> disabledWorlds
) {
    public static Settings from(FileConfiguration cfg) {
        return new Settings(
                cfg.getBoolean("infinite-stock", true),
                cfg.getBoolean("remove-demand-pricing", true),
                cfg.getBoolean("include-wandering-traders", true),
                Set.copyOf(cfg.getStringList("disabled-worlds"))
        );
    }
}
