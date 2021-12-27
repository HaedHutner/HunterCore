package dev.haedhutner.core;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import dev.haedhutner.chat.ChatModule;
import dev.haedhutner.core.combat.CombatLog;
import dev.haedhutner.core.db.DatabaseContext;
import dev.haedhutner.core.event.HunterHibernateInitializedEvent;
import dev.haedhutner.core.module.ModuleEngine;
import dev.haedhutner.core.module.ModuleRegistrationEvent;
import dev.haedhutner.core.serialize.DurationTypeSerializer;
import dev.haedhutner.core.utils.CoreUtils;
import dev.haedhutner.core.utils.SimpleOperationResult;
import dev.haedhutner.parties.PartiesModule;
import dev.haedhutner.skills.SkillsModule;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.time.Duration;
import java.util.Optional;

import static dev.haedhutner.core.HunterCore.*;

@Plugin(id = ID, version = VERSION, name = NAME, description = DESCRIPTION)
public class HunterCore {

    public static final String ID = "huntercore";
    public static final String NAME = "Hunter Core";
    public static final String DESCRIPTION = "Core Utilities";
    public static final String VERSION = "%PROJECT_VERSION%";

    private static HunterCore instance;

    private static boolean init = false;

    @Inject
    Logger logger;

    @Inject
    CoreConfig coreConfig;

    @Inject
    ChatModule chatModule;

    @Inject
    PartiesModule partiesModule;

    @Inject
    SkillsModule skillsModule;

    @Inject
    Injector injector;

    private EconomyService economyService;

    private CombatLog combatLog;

    private DatabaseContext databaseContext;

    private ModuleEngine coreModuleEngine;

    @Listener(order = Order.FIRST)
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Duration.class), new DurationTypeSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Duration.class), new DurationTypeSerializer());

        SimpleOperationResult result = coreConfig.init();
        if (!result.isSuccess()) {
            logger.error(result.toString());
            init = false;
            return;
        }

        if (coreConfig.DB_ENABLED) {
            databaseContext = new DatabaseContext(coreConfig.JPA_CONFIG, logger);
        }

        this.economyService = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);

        this.combatLog = new CombatLog();
        combatLog.init();

        coreModuleEngine = new ModuleEngine(logger);
        coreModuleEngine.registerAndInitModules(coreConfig.MODULES);

        Sponge.getEventManager().post(new HunterHibernateInitializedEvent(databaseContext.getEntityManagerFactory()));
        init = true;
    }

    @Listener
    public void onModuleRegistration(ModuleRegistrationEvent event) {
        event.registerModules(
                chatModule,
                partiesModule,
                skillsModule
        );
    }

    @Listener
    public void onStarted(GameStartedServerEvent event) {
        if (init) {
            coreModuleEngine.startModules();
        }
    }

    @Listener
    public void onStopped(GameStoppedServerEvent event) {
        if (init && coreConfig.DB_ENABLED) {
            databaseContext.close();
        }

        if (init) {
            coreModuleEngine.stopModules();
        }
    }

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @Root EntityDamageSource source, @Getter("getTargetEntity") Player victim) {
        Entity rootEntity = CoreUtils.damageFetchRootEntity(source);

        if (!(rootEntity instanceof Player)) {
            return;
        }

        combatLog.initiateCombat((Player) rootEntity, victim);
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        if (init) {
            coreModuleEngine.reloadModules();
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event, @Root Player attacker, @Getter("getTargetEntity") Player victim) {
        combatLog.endCombat(attacker, victim);
    }

    public Logger getLogger() {
        return logger;
    }

    public ChatModule getChatModule() {
        return chatModule;
    }

    public SkillsModule getSkillsModule() {
        return skillsModule;
    }

    public PartiesModule getPartiesModule() {
        return partiesModule;
    }

    public static CoreConfig getConfig() {
        return getInstance().coreConfig;
    }

    public Injector getInjector() {
        return injector;
    }

    public static HunterCore getInstance() {
        return instance;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return getInstance().getInjector().getInstance(clazz);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return getDatabaseContext().getEntityManagerFactory();
    }

    public static CombatLog getCombatLog() {
        return getInstance().combatLog;
    }

    public static DatabaseContext getDatabaseContext() {
        return getInstance().databaseContext;
    }

    public static Optional<EconomyService> getEconomyService() {
        return Optional.ofNullable(getInstance().economyService);
    }
}
