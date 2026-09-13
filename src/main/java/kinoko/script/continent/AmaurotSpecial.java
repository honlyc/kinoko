package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.script.common.ScriptMessageParam;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.Account;
import kinoko.world.user.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class AmaurotSpecial extends ScriptHandler {

    /**
     * 脚本中心 - 主入口
     * 对应JS脚本中的拍卖行中心脚本
     */
    @Script("AmaurotHelp")
    public static void AmaurotCenter(ScriptManager sm) {
        final User user = sm.getUser();
        final Account account = user.getAccount();

        // 构建标题和玩家信息
        String title = bold("欢迎来到" + red("BeiDou") + "脚本中心") + "\r\n";
        title += "当前点券：" + account.getNxCredit() + "\r\n";
        title += "当前抵用券：" + account.getMaplePoint() + "\r\n";
        title += "当前信用券：" + account.getNxPrepaid() + "\r\n";
        title += "当前金币：" + user.getInventoryManager().getMoney() + "\r\n";
        title += " \r\n";

        // 构建菜单选项（使用LinkedHashMap保持顺序）
        Map<Integer, String> options = new LinkedHashMap<>();
        options.put(3, "传送自由");
        options.put(69, "快速转职");
        options.put(70, "学习技能");
        options.put(71, "超级传送");
        options.put(4, "爆率一览");
        options.put(2, "在线奖励");
        options.put(0, "新人福利");
        options.put(1, "每日签到");
        options.put(72, "转世重生");
        options.put(5, "野外BOSS刷新");
        options.put(51, "矿物背包");

        // GM额外选项
        if (account.isGM()) {
            options.put(61, red("=====以下内容仅GM可见====="));
            options.put(62, "超级商店(GM)");
            options.put(63, "整容集合(GM)");
            options.put(64, "UI查询(GM)");
            options.put(65, "一键删除道具(GM)");
            options.put(66, "一键刷道具(GM)");
        }

        int selection = sm.askMenu(title, options);
        handleSelection(sm, selection);
    }

    /**
     * 处理菜单选择
     */
    private static void handleSelection(ScriptManager sm, int selection) {
        switch (selection) {
            // ===== 普通玩家功能 =====
            case 3 -> {
                // 传送自由 - 传送到自由市场
                sm.setQRValue(QuestRecordType.FreeMarket, "01"); // 默认从Henesys进入
                sm.warp(910000000, "out00");
            }
            case 69 -> quickJobAdvance(sm);
            case 70 -> learnSkill(sm);
            case 71 -> superTeleport(sm);
            case 4 -> dropRateInfo(sm);
            case 2 -> onlineReward(sm);
            case 0 -> newbieWelfare(sm);
            case 1 -> dailySignIn(sm);
            case 72 -> rebirth(sm);
            case 5 -> areaBossRefresh(sm);
            case 51 -> oreBag(sm);

            // ===== GM功能 =====
            case 61 -> {
                // 分隔符选项，不做任何事
                sm.sayOk("请选择具体的GM功能。");
            }
            case 62 -> {
                // 超级商店
                sm.openShopNPC(9900001);
            }
            case 63 -> salon(sm);
            case 64 -> uiQuery(sm);
            case 65 -> deleteItems(sm);
            case 66 -> spawnItems(sm);

            default -> sm.sayOk("该功能暂不支持，敬请期待！");
        }
    }

    // ===== 子功能实现 =====

    /**
     * 快速转职
     */
    private static void quickJobAdvance(ScriptManager sm) {
        // TODO: 实现快速转职功能
        sm.sayOk("快速转职功能开发中，敬请期待！");
    }

    /**
     * 学习技能
     */
    private static void learnSkill(ScriptManager sm) {
        // TODO: 实现技能学习功能
        sm.sayOk("技能学习功能开发中，敬请期待！");
    }

    /**
     * 超级传送
     */
    private static void superTeleport(ScriptManager sm) {
        Map<Integer, String> options = new LinkedHashMap<>();
        options.put(0, "射手村 (Henesys)");
        options.put(1, "勇士部落 (Perion)");
        options.put(2, "魔法密林 (Ellinia)");
        options.put(3, "废弃都市 (Kerning City)");
        options.put(4, "墨镜港 (Lith Harbor)");
        options.put(5, "冰原雪域 (El Nath)");
        options.put(6, "天空之城 (Orbis)");
        options.put(7, "玩具城 (Ludibrium)");
        options.put(8, "神秘岛 (Leafre)");
        options.put(9, "武陵 (Mu Lung)");
        options.put(10, "地球防御总部 (Omega Sector)");
        options.put(11, "水下世界 (Aquarium)");

        int answer = sm.askMenu("选择你想传送的地点：", options);
        switch (answer) {
            case 0 -> sm.warp(100000000);   // Henesys
            case 1 -> sm.warp(102000000);   // Perion
            case 2 -> sm.warp(101000000);   // Ellinia
            case 3 -> sm.warp(103000000);   // Kerning City
            case 4 -> sm.warp(104000000);   // Lith Harbor
            case 5 -> sm.warp(211000000);   // El Nath
            case 6 -> sm.warp(200000000);   // Orbis
            case 7 -> sm.warp(220000000);   // Ludibrium
            case 8 -> sm.warp(240000000);   // Leafre
            case 9 -> sm.warp(250000000);   // Mu Lung
            case 10 -> sm.warp(221000000);  // Omega Sector
            case 11 -> sm.warp(230000000);  // Aquarium
            default -> sm.sayOk("无效的选择。");
        }
    }

    /**
     * 爆率一览 - 显示当前地图掉落信息
     */
    private static void dropRateInfo(ScriptManager sm) {
        // TODO: 实现当前地图掉落查询功能
        int fieldId = sm.getFieldId();
        sm.sayOk("当前地图ID：" + fieldId + "\r\n掉落查询功能开发中，敬请期待！");
    }

    /**
     * 在线奖励
     */
    private static void onlineReward(ScriptManager sm) {
        // TODO: 实现在线奖励功能
        sm.sayOk("在线奖励功能开发中，敬请期待！");
    }

    /**
     * 新人福利
     * 参考JS脚本：新人福利礼包（SpicyBurgerKing）
     * 每个角色限领一次，随机获得金币+点券
     */
    private static void newbieWelfare(ScriptManager sm) {
        final User user = sm.getUser();
        final Account account = user.getAccount();

        // 检查是否已领取（使用QR值标记领取状态）
        final String claimed = sm.getQRValue(QuestRecordType.NewbieWelfare);
        if ("1".equals(claimed)) {
            sm.sayOk("您已经领取了新手奖励了。每个角色" + red("限领一次。"));
            return;
        }

        // 生成随机数量：金币500~1000万，点券100~200万
        final int mesoQty = sm.getRandomIntBelow(501) + 500;   // 500~1000（万为单位）
        final int cashQty = sm.getRandomIntBelow(101) + 100;   // 100~200（万为单位）

        // 确认领取
        if (!sm.askAccept("您确定要领取新手礼包吗？一个角色" + red("限领一次。") + "\r\n\r\n"
                + "获得奖励：\r\n"
                + blue(mesoQty + "") + " 万金币\r\n"
                + blue(cashQty + "") + " 万点券")) {
            sm.sayOk("好吧，下次再来领取吧。");
            return;
        }

        // 标记为已领取
        sm.setQRValue(QuestRecordType.NewbieWelfare, "1");

        // 发放金币（mesoQty是万为单位，乘以10000）
        sm.addMoney(mesoQty * 10000);

        // 发放点券（cashQty是万为单位，乘以10000）
        final int cashAmount = cashQty * 10000;
        account.setNxCredit(account.getNxCredit() + cashAmount);

        // 通知玩家
        sm.sayOk("恭喜您获得：\r\n"
                + blue(mesoQty + "") + " 万金币\r\n"
                + blue(cashQty + "") + " 万点券\r\n\r\n"
                + "祝您游戏愉快！");

        // 广播消息
        sm.broadcastMessage("【新人福利】玩家 [" + user.getCharacterName() + "] 加入游戏领取开荒金币"
                + mesoQty + "万＋点券" + cashQty + "万！");
    }

    /**
     * 每日签到
     */
    private static void dailySignIn(ScriptManager sm) {
        // TODO: 实现每日签到功能
        sm.sayOk("每日签到功能开发中，敬请期待！");
    }

    /**
     * 转世重生
     */
    private static void rebirth(ScriptManager sm) {
        // TODO: 实现转世重生功能
        sm.sayOk("转世重生功能开发中，敬请期待！");
    }

    /**
     * 野外BOSS刷新信息
     */
    private static void areaBossRefresh(ScriptManager sm) {
        // TODO: 实现野外BOSS刷新查询功能
        sm.sayOk("野外BOSS刷新查询功能开发中，敬请期待！");
    }

    /**
     * 矿物背包
     */
    private static void oreBag(ScriptManager sm) {
        // TODO: 实现矿物背包功能
        sm.sayOk("矿物背包功能开发中，敬请期待！");
    }

    // ===== GM专用功能 =====

    /**
     * 整容集合 (GM)
     */
    private static void salon(ScriptManager sm) {
        // TODO: 实现整容集合功能
        sm.sayOk("整容集合功能开发中，敬请期待！");
    }

    /**
     * UI查询 (GM)
     */
    private static void uiQuery(ScriptManager sm) {
        // TODO: 实现UI查询功能
        sm.sayOk("UI查询功能开发中，敬请期待！");
    }

    /**
     * 一键删除道具 (GM)
     */
    private static void deleteItems(ScriptManager sm) {
        // TODO: 实现一键删除道具功能
        sm.sayOk("一键删除道具功能开发中，敬请期待！");
    }

    /**
     * 一键刷道具 (GM)
     */
    private static void spawnItems(ScriptManager sm) {
        // TODO: 实现一键刷道具功能
        sm.sayOk("一键刷道具功能开发中，敬请期待！");
    }

    // ===== 原有脚本 =====

//    @Script("AmaurotHelp")
    public static void AmaurotHelp(ScriptManager sm) {
        int answer = sm.askMenu("当前版本：KinokoG汉化版。你想查看：", Map.of(0, "普通玩家指令", 1, "GM调试指令", 2, "致谢名单"), new ScriptMessageParam[]{ScriptMessageParam.PLAYER_AS_SPEAKER});
        if (answer == 0) {
            sm.sayOk("在对话框输入@help查看。", ScriptMessageParam.PLAYER_AS_SPEAKER);
        } else if (answer == 1) {
            sm.sayOk("在数据库中找到account表里的你的账号，gm值设置成1，然后在对话框输入@help查看。", ScriptMessageParam.PLAYER_AS_SPEAKER);
        } else if (answer == 2) {
            Map<Integer, String> ticketOptions = new LinkedHashMap<>();
            ticketOptions.put(0, "Kinoko");
            ticketOptions.put(1, "KinokoG");
            ticketOptions.put(2, "KinokoG汉化版");
            int ticketAnswer = sm.askMenu("想了解哪个版本？", ticketOptions, ScriptMessageParam.PLAYER_AS_SPEAKER);
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
