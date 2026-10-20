package com.imnikos.infinitetrades;

import io.papermc.paper.event.player.PlayerTradeEvent;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.MerchantRecipe;

/**
 * How vanilla pricing works, and what we change:
 *
 *   final price = base
 *               + demand bonus      (max(0, base * demand * priceMultiplier))  <- REMOVED
 *               + reputation diff   (gossip: hitting, curing, trading)         <- untouched
 *               - Hero of the Village discount                                 <- untouched
 *
 * "demand" rises every restock based on how many times a trade was used, which is
 * the "you trade with me a lot, so it costs more" mechanic. We pin it to 0.
 *
 * Gossip/reputation is never touched, so hitting a villager still raises prices
 * (MINOR_NEGATIVE gossip) and curing a zombie villager still gives the vanilla
 * discount (MAJOR_POSITIVE + MINOR_POSITIVE gossip).
 */
public final class TradeListener implements Listener {

    private final Supplier<Settings> settings;

    public TradeListener(Supplier<Settings> settings) {
        this.settings = settings;
    }

    // ---- 1. Infinite stock: a completed trade never counts as a "use" ----
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTrade(PlayerTradeEvent event) {
        Settings s = settings.get();
        AbstractVillager merchant = event.getVillager();
        if (!applies(merchant, s)) return;

        if (s.infiniteStock()) {
            // Villager XP / levelling and the player's XP orbs still work as vanilla.
            event.setIncreaseTradeUses(false);
        }
        if (s.removeDemandPricing() && merchant instanceof Villager) {
            MerchantRecipe trade = event.getTrade();
            if (trade.getDemand() != 0) {
                trade.setDemand(0);
                event.setTrade(trade);
            }
        }
    }

    // ---- 2. Clean every offer right before the trade window opens ----
    // Vanilla recalculates reputation-based special prices on open; demand is read
    // live from each offer, so zeroing it here is enough.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof AbstractVillager merchant) {
            sanitize(merchant, settings.get());
        }
    }

    // ---- 3. Vanilla restock (villager works at job site) ----
    // Restock is where demand gets recalculated. Keep it at 0 and uses at 0.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onReplenish(VillagerReplenishTradeEvent event) {
        Settings s = settings.get();
        if (!(event.getEntity() instanceof AbstractVillager merchant) || !applies(merchant, s)) return;

        MerchantRecipe recipe = event.getRecipe();
        boolean changed = clean(recipe, s, merchant instanceof Villager);
        if (changed) event.setRecipe(recipe);
    }

    // ---- 4. Fix villagers from before the plugin was installed as chunks load ----
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        Settings s = settings.get();
        for (Entity entity : event.getEntities()) {
            if (entity instanceof AbstractVillager merchant) {
                sanitize(merchant, s);
            }
        }
    }

    // -------------------------------------------------------------------

    private void sanitize(AbstractVillager merchant, Settings s) {
        if (!applies(merchant, s)) return;
        boolean isVillager = merchant instanceof Villager;

        List<MerchantRecipe> recipes = merchant.getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            MerchantRecipe recipe = recipes.get(i);
            if (clean(recipe, s, isVillager)) {
                merchant.setRecipe(i, recipe);
            }
        }
    }

    /** @return true if the recipe was modified */
    private static boolean clean(MerchantRecipe recipe, Settings s, boolean isVillager) {
        boolean changed = false;
        if (s.infiniteStock() && recipe.getUses() != 0) {
            recipe.setUses(0);
            changed = true;
        }
        // Note: maxUses is deliberately left alone. Raising it to Integer.MAX_VALUE
        // would overflow vanilla's demand math on restock.
        if (s.removeDemandPricing() && isVillager && recipe.getDemand() != 0) {
            recipe.setDemand(0);
            changed = true;
        }
        return changed;
    }

    private static boolean applies(AbstractVillager merchant, Settings s) {
        if (s.disabledWorlds().contains(merchant.getWorld().getName())) return false;
        if (merchant instanceof WanderingTrader) return s.includeWanderingTraders();
        return merchant instanceof Villager;
    }
}
