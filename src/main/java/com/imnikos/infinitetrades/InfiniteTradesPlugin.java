package com.imnikos.infinitetrades;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfiniteTradesPlugin extends JavaPlugin {

    private volatile Settings settings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadSettings();

        getServer().getPluginManager().registerEvents(new TradeListener(() -> settings), this);
        registerCommands();

        getLogger().info("Infinite stock: " + settings.infiniteStock()
                + " | Demand pricing removed: " + settings.removeDemandPricing());
    }

    private void reloadSettings() {
        reloadConfig();
        settings = Settings.from(getConfig());
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    Commands.literal("infinitetrades")
                            .requires(src -> src.getSender().hasPermission("infinitetrades.admin"))
                            .then(Commands.literal("reload").executes(ctx -> {
                                reloadSettings();
                                ctx.getSource().getSender().sendMessage(
                                        Component.text("InfiniteTrades config reloaded.", NamedTextColor.GREEN));
                                return Command.SINGLE_SUCCESS;
                            }))
                            .then(Commands.literal("status").executes(ctx -> {
                                Settings s = settings;
                                ctx.getSource().getSender().sendMessage(Component.text(
                                        "Infinite stock: " + s.infiniteStock()
                                                + "\nDemand pricing removed: " + s.removeDemandPricing()
                                                + "\nWandering traders: " + s.includeWanderingTraders()
                                                + "\nDisabled worlds: " + s.disabledWorlds(),
                                        NamedTextColor.AQUA));
                                return Command.SINGLE_SUCCESS;
                            }))
                            .build(),
                    "InfiniteTrades admin command",
                    List.of("itrades"));
        });
    }
}
