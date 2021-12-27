package dev.haedhutner.core;

import com.google.inject.Singleton;
import dev.haedhutner.chat.ChatModule;
import dev.haedhutner.core.db.JPAConfig;
import dev.haedhutner.core.utils.PluginConfig;
import dev.haedhutner.parties.PartiesModule;
import dev.haedhutner.skills.SkillsModule;
import ninja.leaping.configurate.objectmapping.Setting;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.HashMap;

@Singleton
public class CoreConfig extends PluginConfig {

    @Setting("combat-limit")
    public Duration COMBAT_LIMIT = Duration.of(30, ChronoUnit.SECONDS);

    @Setting("db-enabled")
    public boolean DB_ENABLED = true;

    @Setting("jpa")
    public JPAConfig JPA_CONFIG = new JPAConfig();

    @Setting("modules")
    public Map<String, Boolean> MODULES = new HashMap<String, Boolean>() {{
        put(ChatModule.ID, false);
        put(PartiesModule.ID, false);
        put(SkillsModule.ID, false);
    }};

    protected CoreConfig() throws IOException {
        super(Paths.get(".", "config", HunterCore.ID, "config.conf"));
    }
}
