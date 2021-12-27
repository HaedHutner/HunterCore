package dev.haedhutner.skills.api.skill;

import java.util.HashSet;
import java.util.Set;

public abstract class TargetedSkill extends AbstractSkill implements TargetedCastable {

    private Set<String> passableBlocks = new HashSet<>();

    protected TargetedSkill(String id, String name) {
        super(id, name);
    }

    @Override
    public Set<String> getPassableBlocks() {
        return passableBlocks;
    }

    @Override
    public void setPassableBlocks(Set<String> blockIds) {
        passableBlocks = blockIds;
    }
}
