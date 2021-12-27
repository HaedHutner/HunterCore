package dev.haedhutner.skills.api.skill;

import dev.haedhutner.core.HunterCore;

import java.util.Set;

public abstract class TargetedSkill extends AbstractSkill implements TargetedCastable {
    protected TargetedSkill(String id, String name) {
        super(id, name);
    }

    @Override
    public Set<String> getPassableBlocks() {
        return HunterCore.getInstance().getSkillsModule().getConfig().PASSABLE_BLOCKS;
    }

    @Override
    public void setPassableBlocks(Set<String> blockIds) {

    }
}
