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
                int resourceLevel = (int) ((amount / user.getMax()) * config.RESOURCE_MAX_SYMBOLS);

                Text display = Text.of(resourceBars.get(resourceLevel), " ", config.RESOURCE_COLOR_FULL, amount, "/", (int) user.getMax(), " ", config.RESOURCE_NAME);
                p.sendTitle(Title.builder().title(Text.EMPTY).subtitle(Text.EMPTY).actionBar(display).fadeOut(100).build());
            });
        }
    }

    public void onPlayerJoin(Player player) {
        resourceService.getOrCreateUser(player);
    }

    public void generateResourceBars() {
        ImmutableMap.Builder<Integer, Text> builder = ImmutableMap.builder();

        for (int i = 0; i <= config.RESOURCE_MAX_SYMBOLS; i++) {
            builder.put(config.RESOURCE_MAX_SYMBOLS - i, barLevel(config.RESOURCE_MAX_SYMBOLS - i));
        }

        resourceBars = builder.build();
    }

    private Text barLevel(int level) {
        int numberOfEmptyBars = config.RESOURCE_MAX_SYMBOLS - level;

        Text fullBars = Text.of(config.RESOURCE_COLOR_FULL, String.join("", Collections.nCopies(level, config.RESOURCE_SYMBOL_FULL)));
        Text emptyBars = Text.of(config.RESOURCE_COLOR_EMPTY, String.join("", Collections.nCopies(numberOfEmptyBars, config.RESOURCE_SYMBOL_EMPTY)));

        return Text.of(fullBars, emptyBars);
    }
}
