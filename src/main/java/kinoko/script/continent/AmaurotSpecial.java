package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.script.common.ScriptMessageParam;

import java.util.HashMap;
import java.util.Map;

public class AmaurotSpecial extends ScriptHandler {
    @Script("AmaurotHelp")
    public static void AmaurotHelp(ScriptManager sm) {
        int answer = sm.askMenu("当前版本：KinokoG汉化版。你想查看：", Map.of(0, "普通玩家指令", 1, "GM调试指令", 2, "致谢名单"), new ScriptMessageParam[]{ScriptMessageParam.PLAYER_AS_SPEAKER});
        if (answer == 0) {
            sm.sayOk("在对话框输入@help查看。", ScriptMessageParam.PLAYER_AS_SPEAKER);
        } else if (answer == 1) {
            sm.sayOk("在数据库中找到account表里的你的账号，gm值设置成1，然后在对话框输入@help查看。", ScriptMessageParam.PLAYER_AS_SPEAKER);
        } else if (answer == 2) {
            Map<Integer, String> options = new HashMap();
            options.put(0, "Kinoko");
            options.put(1, "KinokoG");
            options.put(2, "KinokoG汉化版");
            int ticketAnswer = sm.askMenu("想了解哪个版本？", options, ScriptMessageParam.PLAYER_AS_SPEAKER);
            if (ticketAnswer == 0) {
                sm.sayNext("#bteto#k(discord.com)", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayPrev("#bteto#k开源了Kinoko的服务端和客户端插件，结合官方GMS095客户端运行。", ScriptMessageParam.PLAYER_AS_SPEAKER);
            } else if (ticketAnswer == 1) {
                sm.sayNext("#bJinwoo#k(discord.com)", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayBoth("#bJinwoo#k在Kinoko的基础上完善了大量npc和任务脚本，并增加了一些联机友好的功能。", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayPrev("#bJinwooh#k慷慨地开源了服务端的全部内容，仓库名为KinokoG(mapleglory)。", ScriptMessageParam.PLAYER_AS_SPEAKER);
            } else if (ticketAnswer == 2) {
                sm.sayNext("《#b逆鳞#k》狭盗团", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayBoth("《#b逆鳞#k》狭盗团对KinokoG进行汉化，对影响体验的部分bug进行修复。", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayBoth("参与成员：\r\n#r团长-#bHaruSusumu灰流苏苏木#k(discord.com)\r\n#r教官-#b江奈Mizuki#k(moguwuyu.com)\r\n#r策划-#b不更文#k(bilibili.com)", new ScriptMessageParam[]{ScriptMessageParam.PLAYER_AS_SPEAKER});
                sm.sayBoth("同时，这个版本能成长和发展，也离不开大家的友好的技术交流氛围！", ScriptMessageParam.PLAYER_AS_SPEAKER);
                sm.sayPrev("特别感谢：\r\n#btest234#k(discord.com)\r\n#bleevccc#k(moguwuyu.com)\r\n#b敬亭幽幽#k(QQ)\r\n#bliar/shinobi9#k(github.com)\r\n#bMaple#k(discord.com)", new ScriptMessageParam[]{ScriptMessageParam.PLAYER_AS_SPEAKER});
            }
        }

    }
}
