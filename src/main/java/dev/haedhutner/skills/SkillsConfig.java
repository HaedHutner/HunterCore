package dev.haedhutner.skills;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.core.module.PluginModule;
import dev.haedhutner.core.module.config.ModuleConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Set;

@Singleton
public class SkillsConfig extends ModuleConfiguration {

    public static final Set<String> DEFAULT_PASSABLE_BLOCKS = Sets.newHashSet(
            BlockTypes.AIR.getId(),
            BlockTypes.TALLGRASS.getId(),
            BlockTypes.SNOW_LAYER.getId(),
            BlockTypes.WATER.getId(),
            BlockTypes.FLOWING_WATER.getId(),
            BlockTypes.GRASS.getId(),
            BlockTypes.WHEAT.getId(),
            BlockTypes.REEDS.getId(),
            BlockTypes.VINE.getId(),
            BlockTypes.DOUBLE_PLANT.getId()
    );

    @Setting("global-cooldown")
    public long GLOBAL_COOLDOWN = 500;

    // Requires Restart
    @Setting("resource-regen-interval-ticks")
    public int RESOURCE_REGEN_TICK_INTERVAL = 20;

    @Setting("resource-regen-rate")
    public double RESOURCE_REGEN_RATE = 5;

    // Requires Restart
    @Setting("resource-limit")
    public double RESOURCE_LIMIT = 100.0d;

    @Setting("resource-name")
    public String RESOURCE_NAME = "Mana";

    @Setting("resource-color-full")
    public TextColor RESOURCE_COLOR_FULL = TextColors.DARK_BLUE;

    @Setting("resource-color-empty")
    public TextColor RESOURCE_COLOR_EMPTY = TextColors.GRAY;

    @Setting("resource-symbol-full")
    public String RESOURCE_SYMBOL_FULL = "▉";

    @Setting("resource-symbol-empty")
    public String RESOURCE_SYMBOL_EMPTY = "▒";

    @Setting("resource-max-symbols")
    public int RESOURCE_MAX_SYMBOLS = 10;

    @Setting("passable-blocks")
    public Set<String> PASSABLE_BLOCKS = DEFAULT_PASSABLE_BLOCKS;

    @Inject
    protected SkillsConfig(PluginContainer plugin) {
        super(plugin, SkillsModule.ID);
    }
}
