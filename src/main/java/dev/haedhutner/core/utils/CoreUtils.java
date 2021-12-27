package dev.haedhutner.core.utils;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Various utility methods. Naming conventions:<br>
 * <ul>
 *     <li>Methods with "Search" in the name do not promise a result ( return an Optional )</li>
 *     <li>Methods with "Fetch" in the name do promise a result ( Will not return null )</li>
 * </ul>
 */
public class CoreUtils {

    public static final List<EquipmentType> EQUIPMENT_SLOTS = Arrays.asList(
            EquipmentTypes.HEADWEAR,
            EquipmentTypes.CHESTPLATE,
            EquipmentTypes.LEGGINGS,
            EquipmentTypes.BOOTS,
            EquipmentTypes.MAIN_HAND,
            EquipmentTypes.OFF_HAND
    );

    static UserStorageService userStorage;

    public static Predicate<BlockRayHit<World>> createBlockFilter(Set<String> passableBlocks) {
        return hit -> {
            BlockType type = hit.getExtent().getBlockType(hit.getBlockPosition());
            return !passableBlocks.contains(type.getId());
        };
    }

    public static Optional<Entity> entitySearchInAllWorldsByUuid(UUID id) {
        for (World world : Sponge.getServer().getWorlds()) {
            Optional<Entity> entity = world.getEntity(id);
            if (entity.isPresent()) {
                return entity;
            }
        }
        return Optional.empty();
    }

    public static Optional<Entity> entitySearchInLineOfSight(Entity source, double distance) {
        return source.getWorld().getIntersectingEntities(source, distance).stream()
                .map(EntityUniverse.EntityHit::getEntity)
                .findFirst();
    }

    public static Optional<Player> damageSearchPlayerSource(EntityDamageSource source) {
        Entity root = damageFetchRootEntity(source);

        return root instanceof Player ? Optional.of((Player) root) : Optional.empty();
    }

    public static Entity damageFetchRootEntity(EntityDamageSource source) {
        if (source instanceof IndirectEntityDamageSource) {
            IndirectEntityDamageSource indirect = (IndirectEntityDamageSource) source;
            return indirect.getIndirectSource();
        }

        return source.getSource();
    }

    public static Optional<List<ItemStack>> inventorySearchEquippedItems(Entity entity) {
        if (!(entity instanceof ArmorEquipable)) {
            return Optional.empty();
        }
        ArmorEquipable equipable = (ArmorEquipable) entity;

        List<ItemStack> equippedItems = new ArrayList<>();

        EQUIPMENT_SLOTS.forEach(type -> equipable.getEquipped(type).ifPresent(equippedItems::add));

        return Optional.of(equippedItems);
    }

    public static Optional<ItemStack> inventorySearchMainHand(Entity entity) {
        return inventorySearchEquipment(entity, EquipmentTypes.MAIN_HAND);
    }

    public static Optional<ItemStack> inventorySearchOffHand(Entity entity) {
        return inventorySearchEquipment(entity, EquipmentTypes.OFF_HAND);
    }

    public static Optional<ItemStack> inventorySearchEquipment(Entity entity, EquipmentType type) {
        if (!(entity instanceof ArmorEquipable)) {
            return Optional.empty();
        }
        ArmorEquipable equipable = (ArmorEquipable) entity;

        return equipable.getEquipped(type);
    }

    public static DataTransactionResult dataApplyPotionEffect(Living living, PotionEffect effect) {
        PotionEffectData effects = living.getOrCreate(PotionEffectData.class).get();
        effects.addElement(effect);
        return living.offer(effects);
    }

    public static DataTransactionResult dataRemovePotionEffect(Living living, PotionEffect effect) {
        PotionEffectData effects = living.getOrCreate(PotionEffectData.class).get();
        effects.removeAll(listEffect -> listEffect.getType() == effect.getType());
        return living.offer(effects);
    }

    public static boolean damageLiving(Living living, DamageSource source, double amount) {
        return living.damage(amount, source);
    }

    public static boolean healLiving(Living living, double amount) {

        double health = living.health().get();
        double maxHealth = living.maxHealth().get();

        double result = health + amount;

        if (result >= maxHealth) result = maxHealth;
        if (result <= 0.0d) result = 0.0d;

        return living.offer(Keys.HEALTH_SCALE, 20.0d).isSuccessful() &&
                living.offer(Keys.HEALTH, result).isSuccessful();
    }

    public static float mathClamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static Text textReplace(Text text, String argument, Text replacement) {
        Text t;

        if (text.getChildren().isEmpty()) {
            t = text;
        } else {
            t = text.toBuilder()
                    .removeAll()
                    .append(text.getChildren().stream().map(x -> textReplace(x, argument, replacement)).collect(Collectors.toList()))
                    .build();
        }

        String plain = t.toPlainSingle();

        if (!plain.contains(argument)) {
            return t;
        }

        if (plain.equals(argument)) {
            return replacement;
        }

        Text.Builder builder = Text.builder();

        List<String> strs = Arrays.asList(plain.split(Pattern.quote(argument)));

        for (String str : Iterables.limit(strs, strs.size() - 1)) {
            builder.append(Text.of(str));
            builder.append(replacement);
        }

        builder.append(Text.of(strs.get(strs.size() - 1)));

        if (plain.endsWith(argument)) {
            builder.append(replacement);
        }

        builder.style(text.getStyle()).color(text.getColor()).append(text.getChildren());
        return builder.build();
    }

    public static Text textFormatDuration(long duration) {
        String format = "H'h' m'm' s.S's'";

        if (duration < 60000) {
            format = "s's'";
        }

        if (duration >= 60000 && duration < 3600000) {
            format = "m'm'";
        }

        if (duration >= 3600000 && duration < 86400000) {
            format = "H'h' m'm'";
        }

        if (duration >= 86400000) {
            format = "d'd' H'h'";
        }

        String formatted = DurationFormatUtils.formatDuration(duration, format, false);
        // Remove the third digit after the decimal
        formatted = new StringBuilder(formatted).deleteCharAt(formatted.length() - 2).toString();

        return Text.of(formatted);
    }

    public static String convertDurationToString(Duration duration) {
        String result = "";
        if (duration != null) {
            long days = duration.toDays();
            duration = duration.minusDays(days);
            long hours = duration.toHours();
            duration = duration.minusHours(hours);
            long minutes = duration.toMinutes();
            duration = duration.minusMinutes(minutes);
            long seconds = duration.getSeconds();
            duration = duration.minusSeconds(seconds);
            long millis = duration.toMillis();
            result = (days == 0 ? "" : days + "d") +
                    (hours == 0 ? "" : hours + "h") +
                    (minutes == 0 ? "" : minutes + "m") +
                    (seconds == 0 ? "" : seconds + "s") +
                    (seconds == 0 ? "" : millis + "M");
        }

        return result;
    }

    public static Duration convertStringToDuration(String durationString) {
        if (StringUtils.isEmpty(durationString)) {
            return null;
        }

        String delimiter = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
        String[] tokens = durationString.split(delimiter);
        Duration result = Duration.ZERO;

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i - 1].matches("\\d+")) {
                long amount = Long.parseLong(tokens[i - 1]);
                switch (tokens[i]) {
                    case "S":
                        result = result.plusMillis(amount);
                        break;
                    case "s":
                        result = result.plusSeconds(amount);
                        break;
                    case "m":
                        result = result.plusMinutes(amount);
                        break;
                    case "h":
                        result = result.plusHours(amount);
                        break;
                    case "d":
                        result = result.plusDays(amount);
                        break;
                }
            }

        }

        return result;
    }

    /**
     * @param uuid the UUID of the player
     * @return An offline User object, or an online Player object. If neither is available, returns
     * empty Optional.
     */
    public static Optional<? extends User> userSearchByUuid(UUID uuid) {
        if (userStorage == null) {
            userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
        }

        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(uuid);

        return onlinePlayer.isPresent() ? onlinePlayer : userStorage.get(uuid);
    }

    /**
     * @param name the name of the player
     */
    public static Optional<? extends User> userSearchByName(String name) {
        if (userStorage == null) {
            userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
        }

        Optional<? extends User> onlinePlayer = Sponge.getServer().getPlayer(name);

        return onlinePlayer.isPresent() ? onlinePlayer : userStorage.get(name);
    }
}
