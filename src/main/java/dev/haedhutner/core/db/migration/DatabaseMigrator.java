package dev.haedhutner.core.db.migration;

import dev.haedhutner.core.db.JPAConfig;
import dev.haedhutner.core.event.HunterDatabaseMigrationEvent;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

public class DatabaseMigrator {

    private final JPAConfig config;

    private final Logger logger;

    public DatabaseMigrator(JPAConfig config, Logger logger) {
        this.logger = logger;
        this.config = config;
    }

    public void migrate() {
        logger.info("Beginning database migration...");

        String vendor = config.HIBERNATE.get(JPAConfig.URL_KEY).split(":")[1];

        HunterDatabaseMigrationEvent event = new HunterDatabaseMigrationEvent();
        Sponge.getEventManager().post(event);

        event.getPluginIds().forEach(pluginId -> {
            String location = String.format("classpath:db/migration/%s/%s", pluginId, vendor);
            logger.info("Migrating " + location);

            FluentConfiguration cfg = new FluentConfiguration()
                    .dataSource(
                            config.HIBERNATE.get(JPAConfig.URL_KEY),
                            config.HIBERNATE.get(JPAConfig.USERNAME_KEY),
                            config.HIBERNATE.get(JPAConfig.PASSWORD_KEY)
                    )
                    .schemas(pluginId)
                    .table("flyway_schema_history_" + pluginId)
                    .locations(location);

            new Flyway(cfg).migrate();
        });

        logger.info("Database migration complete.");
    }

}
