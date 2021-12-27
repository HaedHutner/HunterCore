package dev.haedhutner.parties.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.core.utils.CoreUtils;
import dev.haedhutner.parties.facade.PartyFacade;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;

@Singleton
public class PlayerPartyListener {

    @Inject
    PartyFacade partyFacade;

    @Listener(order = Order.LAST)
    public void onPlayerDamage(DamageEntityEvent event, @Root EntityDamageSource source, @Getter("getTargetEntity") Player target) {
        CoreUtils.damageSearchPlayerSource(source).ifPresent(player -> {
            partyFacade.onPlayerAttack(event, player, target);
        });
    }
}
