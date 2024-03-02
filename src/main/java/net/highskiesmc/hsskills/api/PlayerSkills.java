package net.highskiesmc.hsskills.api;

import net.highskiesmc.hsskills.api.Skills.Skill;

import java.util.List;

public class PlayerSkills {
    private final List<Skill> skills;
    private int tokens;

    public PlayerSkills(List<Skill> skills, int tokens) {
        this.skills = skills;
        this.tokens = tokens;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }
    public void addSkills(List<Skill> skills) {
        this.skills.addAll(skills);
    }

    public int getTokens() {
        return this.tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}
