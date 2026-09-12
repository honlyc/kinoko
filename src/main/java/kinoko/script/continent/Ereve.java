package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public class Ereve extends ScriptHandler {
    @Script("cygnus_lv120")
    public static void cygnus_lv120(ScriptManager sm) {
        // Kidan : Knight Trainer (1102003)
        //   Empress' Road : Knights Chamber (130000100)
        //   Empress's Road : Knights Chamber (130000101)
        sm.sayOk("Welcome to the Hall of Knights.");
    }
    @Script("dealerA")
    public static void dealerA(ScriptManager sm) {
        // Dealer A (1102004)
        //   Empress' Road : Knights Chamber (130000100)
        //   Empress's Road : Knights Chamber (130000101)
        sm.sayOk("热更新脚本测试.");
    }
}
