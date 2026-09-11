package kinoko.world.job.gm;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.field.Field;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.Set;

public class Admin extends SkillProcessor {
    // GM
    public static final int HASTE = 9001000;
    public static final int SUPER_DRAGON_ROAR = 9001001;
    public static final int TELEPORT = 9001002;

    // SUPER-GM
    public static final int HEAL_DISPEL = 9101000;
    public static final int SUPER_HASTE = 9101001;

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case HEAL_DISPEL -> {
                // Heal
                final int healPercentage = si.getValue(SkillStat.hp, slv) / skill.getAffectedMemberCount();
                user.addHp(user.getMaxHp() * healPercentage / 100);
                // Dispel
                user.resetTemporaryStat(Set.of(
                        CharacterTemporaryStat.Poison,
                        CharacterTemporaryStat.Seal,
                        CharacterTemporaryStat.Darkness,
                        CharacterTemporaryStat.Weakness,
                        CharacterTemporaryStat.Curse,
                        CharacterTemporaryStat.Slow
                ));
                return;
            }
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}
