package net.highskiesmc.hsskills.api.Skills;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum Skill {
    //<editor-fold desc="Island">
    MOB_XP_INCREASE(SkillType.ISLAND, 25, (Skill skill) -> "+" + skill.amount + "% XP from Mob Spawner Mobs"),
    // Percent Chance
    RESOURCE_NODE_INSTANT_RESPAWN_CHANCE(SkillType.ISLAND, 5, "Resource Node Insta-respawn chance"),
    ISLAND_SKILLS_LEVEL_FASTER(SkillType.ISLAND, 200, (Skill skill) -> "/is skills level " + skill.amount + "% faster"),
    FLY(SkillType.ISLAND, 0, "/fly in all non-pvp zones"),
    DURABILITY_LOSS_DECREASE_ON_ISLAND(SkillType.ISLAND, 90, (Skill skill) -> "-" + skill.amount + "% Durability loss" +
            " in the island world"),

    //</editor-fold>
    //<editor-fold desc="PvP">
    MAX_HP(SkillType.PVP, 2, (Skill skill) -> "+" + skill.amount + " Max HP"),
    BLEED(SkillType.PVP, 0, "Bleed Passive"),
    IGNORE_SHIELD(SkillType.PVP, 50, (Skill skill) -> skill.amount + "% Chance to Ignore Shields"),
    ARMOR_DURABILITY_INCREASE(SkillType.PVP, 2, (Skill skill) -> skill.amount + "x Armor Durability"),
    DUAL_WIELD(SkillType.PVP, 0, (Skill skill) -> "Dual Wield Ability"),

    //</editor-fold>
    //<editor-fold desc="PvE">
    OUTGOING_PVE_DAMAGE_INCREASE(SkillType.PVE, 10, (Skill skill) -> "+" + skill.amount + "% Outgoing PvE Damage"),
    FOOD_LOSS_DECREASE(SkillType.PVE, 50, (Skill skill) -> "-" + skill.amount + "% Food Loss"),
    ADVENTURE_LEAVE_TIMER_DECREASE(SkillType.PVE, 50, (Skill skill) -> "-" + skill.amount + "% /adventure leave timer"),
    INSTANT_HEALTH_INCREASE(SkillType.PVE, 25, (Skill skill) -> "Instant Potions heal " + skill.amount + "% more"),
    // Percent Chance to get Mob Loot
    ADVENTURE_MOB_LOOT(SkillType.PVE, 5, "Adventure Mob Loot");

    //</editor-fold>

    private final SkillType type;
    private final String description;
    private final int amount;

    Skill(SkillType type, int amount, String desc) {
        this.type = type;
        this.amount = amount;
        this.description = desc;
    }

    Skill(SkillType type, int amount, Function<Skill, String> descSupplier) {
        this.type = type;
        this.amount = amount;
        this.description = descSupplier.apply(this);
    }

    public static List<Skill> getSkills(SkillType type) {
        return Arrays.stream(Skill.values()).filter(x -> x.type == type).toList();
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public SkillType getType() {
        return type;
    }
}
