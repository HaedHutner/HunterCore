package dev.haedhutner.parties.data;

import dev.haedhutner.parties.entity.Party;
import dev.haedhutner.parties.service.PartyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.UUID;

public class PartyData extends AbstractData<PartyData, PartyData.Immutable> {

    private UUID partyUUID;

    PartyData() {
        partyUUID = null;
        registerGettersAndSetters();
    }

    public PartyData(UUID partyUUID) {
        this.partyUUID = partyUUID;
        registerGettersAndSetters();
    }

    @Override
    public void registerGettersAndSetters() {
        registerFieldGetter(PartyKeys.PARTY, this::getPartyUUID);
        registerFieldSetter(PartyKeys.PARTY, this::setPartyUUID);
        registerKeyValue(PartyKeys.PARTY, this::partyUUID);
    }

    public Optional<Party> getParty() {
        return PartyService.getParty(partyUUID);
    }

    public UUID getPartyUUID() {
        return partyUUID;
    }

    public void setPartyUUID(UUID partyUUID) {
        this.partyUUID = partyUUID;
    }

    public Value<UUID> partyUUID() {
        return Sponge.getRegistry().getValueFactory().createValue(PartyKeys.PARTY, partyUUID);
    }

    @Override
    public Optional<PartyData> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(PartyData.class).ifPresent(that -> {
            PartyData data = overlap.merge(this, that);
            this.partyUUID = data.partyUUID;
        });
        return Optional.of(this);
    }

    @Override
    public Optional<PartyData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<PartyData> from(DataView container) {
        container.getString(PartyKeys.PARTY.getQuery()).ifPresent(v -> partyUUID = UUID.fromString(v));
        return Optional.of(this);
    }

    @Override
    public PartyData copy() {
        return new PartyData(partyUUID);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(partyUUID);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(PartyKeys.PARTY.getQuery(), partyUUID.toString());
    }

    public static class Immutable extends AbstractImmutableData<Immutable, PartyData> {

        private final UUID partyUUID;

        {
            registerGetters();
        }

        Immutable() {
            this.partyUUID = null;
        }

        Immutable(UUID partyUUID) {
            this.partyUUID = partyUUID;
        }

        @Override
        protected void registerGetters() {
            registerFieldGetter(PartyKeys.PARTY, this::getPartyUUID);
            registerKeyValue(PartyKeys.PARTY, this::partyUUID);
        }

        public UUID getPartyUUID() {
            return partyUUID;
        }

        public ImmutableValue<UUID> partyUUID() {
            return Sponge.getRegistry().getValueFactory().createValue(PartyKeys.PARTY, partyUUID).asImmutable();
        }

        @Override
        public PartyData asMutable() {
            return new PartyData(this.partyUUID);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(PartyKeys.PARTY.getQuery(), this.partyUUID);
        }

    }

    public static class Builder extends AbstractDataBuilder<PartyData> implements DataManipulatorBuilder<PartyData, Immutable> {

        public Builder() {
            super(PartyData.class, 1);
        }

        @Override
        public PartyData create() {
            return new PartyData();
        }

        @Override
        public Optional<PartyData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<PartyData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }

    }
}
