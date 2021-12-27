package dev.haedhutner.skills.facade;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.skills.SkillsConfig;
import dev.haedhutner.skills.api.event.ResourceEvent;
import dev.haedhutner.skills.api.resource.ResourceUser;
import dev.haedhutner.skills.model.EntityResourceUser;
import dev.haedhutner.skills.service.ResourceService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.Collections;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.GRAY;

@Singleton
public class ResourceFacade {

    private static final int MAX_NUMBER_BARS = 10;

    private ResourceService resourceService;

    private SkillsConfig config;

    private ImmutableMap<Integer, Text> resourceBars;

    @Inject
    public ResourceFacade(ResourceService resourceService, SkillsConfig config) {
        this.resourceService = resourceService;
        this.config = config;
    }


    public void onResourceRegen(ResourceEvent.Regen event) {
        if (resourceBars == null) {
            generateResourceBars();
        }

        if (event.getRegenAmount() > 0 && event.getResourceUser() instanceof EntityResourceUser) {
            Optional<Player> player = Sponge.getServer().getPlayer(((EntityResourceUser) event.getResourceUser()).getId());
            ResourceUser user = event.getResourceUser();

            player.ifPresent(p -> {
                int amount = (int) (event.getRegenAmount() + user.getCurrent());
                amount = amount < user.getMax() ? amount : (int) user.getMax();
                int resourceLevel = (int) ((amount / user.getMax()) * 10);

                Text display = Text.of(resourceBars.get(resourceLevel), " ", config.RESOURCE_COLOR_FULL, amount, "/", (int) user.getMax(), " ", config.RESOURCE_NAME);
                p.sendTitle(Title.builder().title(Text.EMPTY).subtitle(Text.EMPTY).actionBar(display).fadeOut(100).build());
            });
        }
    }

    public void onPlayerJoin(Player player) {
        resourceService.getOrCreateUser(player);
    }

    private void generateResourceBars() {
        resourceBars = ImmutableMap.<Integer, Text>builder()
                .put(MAX_NUMBER_BARS - 10, barLevel(MAX_NUMBER_BARS - 10))
                .put(MAX_NUMBER_BARS - 9, barLevel(MAX_NUMBER_BARS - 9))
                .put(MAX_NUMBER_BARS - 8, barLevel(MAX_NUMBER_BARS - 8))
                .put(MAX_NUMBER_BARS - 7, barLevel(MAX_NUMBER_BARS - 7))
                .put(MAX_NUMBER_BARS - 6, barLevel(MAX_NUMBER_BARS - 6))
                .put(MAX_NUMBER_BARS - 5, barLevel(MAX_NUMBER_BARS - 5))
                .put(MAX_NUMBER_BARS - 4, barLevel(MAX_NUMBER_BARS - 4))
                .put(MAX_NUMBER_BARS - 3, barLevel(MAX_NUMBER_BARS - 3))
                .put(MAX_NUMBER_BARS - 2, barLevel(MAX_NUMBER_BARS - 2))
                .put(MAX_NUMBER_BARS - 1, barLevel(MAX_NUMBER_BARS - 1))
                .put(MAX_NUMBER_BARS, barLevel(MAX_NUMBER_BARS))
                .build();
    }

    private Text barLevel(int level) {
        int numberOfEmptyBars = MAX_NUMBER_BARS - level;

        Text fullBars = Text.of(config.RESOURCE_COLOR_FULL, String.join("", Collections.nCopies(level, config.RESOURCE_SYMBOL_FULL)));
        Text emptyBars = Text.of(config.RESOURCE_COLOR_EMPTY, String.join("", Collections.nCopies(numberOfEmptyBars, config.RESOURCE_SYMBOL_EMPTY)));

        return Text.of(fullBars, emptyBars);
    }
}
