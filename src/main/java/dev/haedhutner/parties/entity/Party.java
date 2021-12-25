package dev.haedhutner.parties.entity;

import dev.haedhutner.core.db.SpongeIdentifiable;

import javax.annotation.Nonnull;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Party implements SpongeIdentifiable {

    @Id
    private UUID uuid;

    private UUID leader;

    @ElementCollection
    private Set<UUID> members;

    private boolean pvp;

    public Party(UUID leader, Set<UUID> members) {
        this.uuid = UUID.randomUUID();
        this.leader = leader;
        this.members = members;
    }

    public Party() {
    }

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public void addMember(UUID member) {
        members.add(member);
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return Objects.equals(uuid, party.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
