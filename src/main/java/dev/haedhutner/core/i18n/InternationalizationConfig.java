package dev.haedhutner.core.i18n;

import dev.haedhutner.core.utils.PluginConfig;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Paths;

public class InternationalizationConfig extends PluginConfig {

    public InternationalizationConfig(PluginContainer plugin, String locale) {
        super(Paths.get(".", "config", plugin.getId(), "i18n", String.format("%s.conf", locale)));
    }
}
