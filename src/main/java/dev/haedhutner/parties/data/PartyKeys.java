package dev.haedhutner.parties.data;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.UUID;

public class PartyKeys {

    public static Key<Value<UUID>> PARTY;

    public static DataRegistration<PartyData, PartyData.Immutable> PARTY_DATA_REGISTRATION;

    static {
        PARTY = Key.builder()
                .type(new TypeToken<Value<UUID>>() {})
                .id("party")
                .name("Party")
                .query(DataQuery.of("huntercore", "Party"))
                .build();
    }

}
