package kinoko.provider.quest.act;

import kinoko.packet.world.MessagePacket;
import kinoko.util.Util;
import kinoko.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(User user, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
        int calcExp = exp * Util.getQuestRateByMap(user.getFieldId());
        user.addExp(calcExp);
        user.write(MessagePacket.incExp(calcExp, 0, true, true));
        return true;
    }
}
