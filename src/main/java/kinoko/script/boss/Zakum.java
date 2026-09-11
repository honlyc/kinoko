/* Adobis
 *
 * El Nath: The Door to Zakum (211042300)
 *
 * Zakum Quest NPC

 * Custom Quest 100200 = whether you can do Zakum
 * Custom Quest 100201 = Collecting Gold Teeth <- indicates it's been started
 * Custom Quest 100203 = Collecting Gold Teeth <- indicates it's finished
 * Quest 7000 - Indicates if you've cleared first stage / fail
 * 4031061 = Piece of Fire Ore - stage 1 reward
 * 4031062 = Breath of Fire    - stage 2 reward
 * 4001017 = Eye of Fire       - stage 3 reward
 * 4000082 = Zombie's Gold Tooth (stage 3 req)
 */

package kinoko.script.boss;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.mob.MobType;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Zakum extends ScriptHandler {
    final static int FIRST_STAGE_MAP = 280010000;
    final static int ZAKUM_BOSS_MAP = 280030000;
    @Script("Zakum00")
    public static void zakum00(ScriptManager sm) {
        // Adobis (2030008)
        //   El Nath : The Door to Zakum (211042300)
        //   Dead Mine : The Door to Chaos Zakum (211042301)
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        if (sm.getLevel() < 50) {
            sm.sayOk("等你变强了再来找我。我这辈子见过不少冒险家，但你实在太弱了，根本无法完成我的任务。");
            return;
        }

        // Initial dialog
        sm.sayOk("嘘……安静点。地牢深处沉睡着一个强大的敌人。按照等级顺序完成任务，你就能见到扎库姆地牢的BOSS了。这绝非易事……但请尽力而为。");

        // Ask the player which quest they want to complete
        int choice = sm.askMenu("嗯……好吧。你看起来完全有能力胜任这个工作。你想先处理哪些任务呢？#b", Map.of(
                0, "探索死亡矿井。（第1关）",
                1, "勘察扎库姆地牢。（第2关）",
                2, "请求精炼物品。（第3关）",
                3, "了解任务简报。")
        );

        switch (choice) {
            case 0:
                if (sm.getQRValue(QuestRecordType.ZakumPreqStageOne).equals("3")) {
                    sm.sayOk("你今天已经三次进入死亡矿洞的洞穴了，因此我不能再让你进去了。请明天再来。");
                    return;
                }

                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("您当前未加入任何队伍。您只能以队伍的形式完成此任务。");
                    return;
                }

                if(!sm.getUser().isPartyLeader()) {
                    sm.sayNext("这段旅程将是一个永无止境的迷宫，其中的任务你无法独自完成。但如果你愿意接受挑战，那么请前往艾尔纳斯的酋长官邸，与你的职业酋长交谈以领取任务。");
                    sm.sayBoth("接到任务后，你可以选择加入一个队伍，或者自己组建一个队伍，然后让队伍的队长与我对话以开始任务。准备好后，请让队伍的队长来找我并对话。");
                    return;
                }

                sm.removeItem(4001015);
                sm.removeItem(4001016);
                sm.removeItem(4001018);
                sm.forceStartQuest(100200);
                for (var member : sm.getField().getUserPool().getPartyMembers(sm.getUser().getPartyId())) {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        if (!partyMember.getQuestManager().hasQuestStarted(7000000)) {
                            sm.message("你的队伍中有成员还没有在艾尔纳斯的职业酋长那里领取任务。");
                            shouldStop.set(true);
                            break;
                        }
                    }
                }

                if (shouldStop.get()) {
                    return;
                }

                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        partyMember.getInventoryManager().removeItem(4001015, partyMember.getInventoryManager().getItemCount(4001015));
                        partyMember.getInventoryManager().removeItem(4001016, partyMember.getInventoryManager().getItemCount(4001016));
                        partyMember.getInventoryManager().removeItem(4031061, partyMember.getInventoryManager().getItemCount(4031061));
                        partyMember.getQuestManager().forceStartQuest(100200);
                    }
                });

                sm.setQRValue(QuestRecordType.ZakumPreqStageOne, sm.getUser().getCharacterName());
                sm.playPortalSE();
                sm.partyWarpInstance(280010000, "st00", 211042300, 30 * 60);
                break;

            case 1:
                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("您当前未加入任何队伍。您只能以队伍的形式完成此任务。");
                    return;
                }

                // Check if Quest 1 is completed
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        if (!partyMember.getQuestManager().hasQuestCompleted(100200)) {
                            shouldStop.set(true);
                        }
                    }
                });

                if (!sm.hasQuestCompleted(100200) || shouldStop.get()) {
                    sm.sayOk("看起来你或你的队伍中还没有人通关上一关。请先通关上一关，再进入下一关。");
                    return;
                }

                // Check if still in the middle of Quest 1
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        if (partyMember.getQuestManager().hasQuestStarted(100200)) {
                            shouldStop.set(true);
                        }
                    }
                });

                if(sm.hasQuestStarted(100200) || shouldStop.get()) {
                    sm.sayOk("看起来你或你们队伍中的某个人在第一关的中途。你必须先通关这一关，才能进入第二关。请先通关第一关。");
                    return;
                }

                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        if (partyMember.getQuestManager().hasQuestStarted(100201)) {
                            shouldStop.set(true);
                        }
                    }
                });

                if (sm.hasQuestStarted(100201) || shouldStop.get()) {
                    if(!sm.askYesNo("嗯……你或者你们队伍中的某个人之前肯定尝试过这个任务，但中途放弃了。你怎么看？你想重试这个关卡吗？")) {
                        sm.sayOk("我明白了……但如果你决定改变主意，请告诉我。");
                        return;
                    }
                } else if (sm.hasQuestCompleted(100201) || shouldStop.get()) {
                    if(!sm.askYesNo("嗯……你或你的队伍中有人已经通关过这一关。如果你想要再次获得奖励，你需要从第一关重新开始任务。否则，你仍然可以完成任务，但不会获得奖励。你还想重试这一关吗？")) {
                        sm.sayOk("我明白了……但如果你决定改变主意，请告诉我。");
                        return;
                    }
                }

                if (!sm.askYesNo("你已经安全通过了第一关。不过，在遇到扎库姆地牢的BOSS之前，还有很长的路要走。那么，你怎么看？你准备好进入下一关了吗？")) {
                    sm.sayOk("我明白了……但如果你决定改变主意，请告诉我。");
                    return;
                }

                sm.sayNext("好的！从现在开始，你将传送至一个地图，那里障碍重重。地图的最深处会站着一个人，如果你与她对话，你将获得一个物品，该物品可以作为材料来制作一个能召唤扎库姆地牢首领的物品。请帮我拿到那个物品。祝你好运！");
                sm.forceStartQuest(100201);
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    try (var lockedMember = member.acquire()) {
                        final User partyMember = lockedMember.get();

                        partyMember.getQuestManager().forceStartQuest(100201);
                    }
                });

                sm.partyWarpInstance(280020000, "sp", 211042300, 30 * 60);

                break;

            case 2:
                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("您当前未加入任何队伍。您只能以队伍的形式完成此任务。");
                    return;
                }

                // Same logic as Quest 2 for Quest 3
                if (!sm.hasQuestCompleted(100201)) {
                    sm.sayOk("嗯……我觉得你还没有通过上一关。请先通关上一关，再进入下一关。");
                    return;
                }

                if (sm.hasQuestStarted(100202)) {
                    if (!sm.hasItem(4000082, 30)) {
                        sm.sayOk("我觉得你还没有#b30个僵尸丢失的金牙#k。把它们都收集起来，我或许能提炼它们，为你制作一件特殊物品……");
                        return;
                    }
                    sm.sayNext("哈哈哈，别担心，我马上就能搞定！");
                    if(!sm.hasItem(4000082, 30) || !sm.hasItem(4001018, 1) || !sm.hasItem(4031062, 1) || !sm.canAddItem(4001017, 1)) {
                        sm.sayOk("嗯……你确定你带齐了制作#r火之眼#k所需的所有物品吗？如果带了，请检查一下你的物品栏是否已满。");
                        return;
                    }

                    sm.addItem(4001017, 5); // Eye of Fire
                    sm.forceCompleteQuest(100202);
                    sm.sayOk("到了。当左边的门打开时，你现在可以进入扎库姆地牢的祭坛了。你需要\\r\\n#b#t4001017##k才能穿过门进入舞台。现在，让我们看看有多少人能进入这个地方……？");
                }
                else if (sm.hasQuestCompleted(3)) {
                    if (!sm.askYesNo("嗯……你不是之前精炼过#b#t4001017##k的那位吗？那么我能为你做些什么呢？你是否对再次将#b#t4031061##k与#b#t4031062##k混合以创造#b#t4001017##k感兴趣？")) {
                        sm.sayOk("我明白了……但请注意，如果没有#b#t4001017##k，你将无法见到扎库姆地下城的头目。");
                        return;
                    }
                    sm.sayOk("嗯，通过将#b#t4031061##k与#b#t4031062##k混合，我可以制作出一个用来召唤首领的祭品，叫做#b#t4001017##k。问题是……（咳嗽咳嗽）如你所见，我最近感觉不太好，所以很难四处走动收集物品。嗯……你能帮我收集#b30个僵尸丢失的金牙#k吗？不过别问我打算在哪里使用它……");
                    sm.forceStartQuest(100202);
                }
                else {
                    sm.sayOk("嗯，通过将#b#t4031061##k与#b#t4031062##k混合，我可以制作出一个用来召唤首领的祭品，叫做#b#t4001017##k。问题是……（咳嗽咳嗽）如你所见，我最近感觉不太好，所以很难四处走动收集物品。嗯……你能帮我收集#b30个僵尸丢失的金牙#k吗？不过别问我打算在哪里使用它……");
                    sm.forceStartQuest(100202);
                }
                break;

            case 3:
                sm.sayNext("不知道从哪里开始吗？为了完成这个任务，你必须得到你所在职业的负责人的批准。我不想因为未经正当程序就让人进来而事后受到责备。我只能让那些已经获得批准的成员组成的团队进来。");
                sm.sayBoth("按照等级顺序完成任务，你就能见到扎库姆地下城的BOSS。收集我向你要的物品，我会把它们做成祭品。把祭品放在祭坛上，你就能看到你想看的东西。为此，先在死亡矿井里搜寻，带回#b#t4001018##k。");
                sm.sayBoth("在那里，除了#b#t4001018##k，你还会找到纸质文件。把它交给#b#p2032002##k，你可能会得到一些有用的东西，以及一块火矿石。接下来，穿过熔岩区，找到#b#t4031062##k。这将是一条危险的道路，但……就制作祭祀物品而言，这是必需品。");
                sm.sayBoth("一旦你获得了#b#t4031062##k，你需要精炼你在1级和2级时获得的#b火矿石碎片#k和#b#t4031062##k。不过别担心，我可以帮你精炼。一旦你全部完成，剩下的任务就是去见扎库姆地牢的BOSS。这并不容易，但请尽力而为。");
                break;
            default:
                return;
        }
    }

    @Script("Zakum01")
    public static void zakum01(ScriptManager sm) {
        // Aura (2032002)
        //   Adobis's Mission I : Unknown Dead Mine (280010000)
        sm.sayNext("你是那个想要调查死亡矿井的人。你需要收集必要的物品，以达到你的最终目标：与扎库姆地牢的BOSS会面。为了获得那个物品，你首先需要获得制作该物品的材料，对吧？你可以在这里获得其中一种材料#b#t4031061##k，不过这并不容易……");
        sm.sayNext("这里有一个入口，通往许多洞穴。进入洞穴后，你会看到一些箱子。将它们全部摧毁，并收集#t4001016#s#k中的#b7个。攻击技能无法摧毁箱子，只有普通的、基本的攻击才有效。之后，收集7把钥匙，进入最里面的房间，那里有宝箱。将钥匙投入宝箱中，即可获得#b#t4031061##k。投入钥匙后需要等待一段时间才能获得，所以请耐心等待。");
        sm.sayNext("当然，并不是每个盒子里都装有#t4001016#。你们都会遇到一些非常意外的情况，所以请注意这一点。偶尔，在整理盒子的过程中，#t4001015#会突然出现。把它们也收集起来，肯定会有好事发生。你们至少需要收集30个#t4001015#。目前我只能告诉你们这些。");
        final int answer = sm.askMenu("你有什么问题要问吗？", Map.of(
                0, "我把#t4031061#带来了。",
                1, "算了，不做任务了，我要离开这里。"
        ));

        if(answer == 0) {
            if (!sm.getQRValue(QuestRecordType.ZakumPreqStageOne).equals(sm.getUser().getCharacterName())) {
                sm.sayOk("一旦你在洞穴中的巨大宝箱处投入7个#b#t4001016#s#k，获得#b#t4031061##k，请将该物品交给队伍队长。当队伍队长持有#b#t4031061##k并与我对话时，即表示你已通关第一关。");
                return;
            }

            if(!sm.hasItem(4031061, 1)) {
                sm.sayOk("我猜你还没拿到#b#t4031061##k吧。请在规定时间内搜遍这里的各个宝箱，收集#t4001016#s#k中的#b7个，并将它们全部投放到洞穴最深处的宝箱中，以收集#b#t4031061##k。一旦你获得该物品，请交给我。");
                return;
            }

            if(!sm.hasItem(4001015)) {
                if(!sm.askYesNo("你安全地带回了#b1个#t4031061##k，但看起来你并没有带回#b#t4001015#。这就是你们团队收集到的全部吗？")) {
                    sm.sayOk("队员们从山洞里收集到的所有物品都应该交给队长，然后队长会全部交给我。请再仔细检查一遍。");
                    return;
                }
            } else {
                if (!sm.askYesNo("你带回了#b1个#t4031061##k和#b" + sm.getItemCount(4001015) + "个#t4001015##k。这是你们队伍成员收集到的所有物品吗？")) {
                    sm.sayOk("队员们从山洞里收集到的所有物品都应该交给队长，然后队长会全部交给我。请再仔细检查一遍。");
                    return;
                }
            }

            if(!sm.removeItem(4031061, 1)) {
                sm.sayOk("请检查并确认您是否携带了#b1个#t4031061##k。");
                return;
            }

            sm.sayOk("好的。利用下面建好的传送门，你们可以回到阿多比斯所在的地图。在使用传送门时，我会把用你们交给我的#b#t4031061##k制成的#b#t4001018##k分发给队伍里的每一位成员。恭喜你们通关第一关。再见……");

            sm.forceCompleteQuest(100200);
            sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                try (var lockedMember = member.acquire()) {
                    final User partyMember = lockedMember.get();

                    partyMember.getQuestManager().forceCompleteQuest(100200);
                }
            });
        } else if(answer == 1) {
            if (sm.askYesNo("如果你在任务进行到一半时退出，你将不得不重新开始……不仅如此，由于这是一个团队任务，即使只有一个玩家决定离开，也可能很难通关。你确定要离开吗？")) {
                sm.sayOk("好的，我会把你送到出口地图那里。#b#p2030011##k会在那里等你。去跟他说话，他会带你出去。再见……");
                sm.partyWarp(280090000, "st00");
            }
        }
    }

    @Script("go280010000")
    public static void go280010000(ScriptManager sm) {
        // go280010000 (2110000)
        //   Adobis's Mission I : Area 1-2 (280010011)
        //   Adobis's Mission I : Area 3-2 (280010031)
        //   Adobis's Mission I : Area 4-2 (280010041)
        //   Adobis's Mission I : Area 7-2 (280010071)
        //   Adobis's Mission I : Area 8-2 (280010081)
        //   Adobis's Mission I : Area 9-2 (280010091)
        //   Adobis's Mission I : Area 11-1 (280010110)
        //   Adobis's Mission I : Area 14-1 (280010140)
        //   Adobis's Mission I : Area 16 <A Dead Mine Somewhere> (280011000)
        //   Adobis's Mission I : Area 16-1 (280011001)
        //   Adobis's Mission I : Area 16-2 (280011002)
        //   Adobis's Mission I : Area 16-3 (280011003)
        //   Adobis's Mission I : Area 16-4 (280011004)
        //   Adobis's Mission I : Area 16-5 (280011005)
        //   Adobis's Mission I : Area 16-6 (280011006)
        sm.warp(280010000);
    }

    @Script("boxBItem0")
    public static void boxbitem0(ScriptManager sm) {
        // boxBItem0 (2112014)
        //   Adobis's Mission I : Area 16-5 (280011005)
        sm.dropRewards(List.of(
                Reward.item(4031061, 1, 1, 1)
        ));
    }

    @Script("Zakum03")
    public static void zakum03(ScriptManager sm) {
        // Adobis's Mission I : Unknown Dead Mine (280010000)
        //   ps01 (440, 193)
        if(sm.hasQuestCompleted(100200)) {
            if(!sm.canAddItem(4001018, 1)) {
                sm.sayOk("请为#b#t4001018##k腾出空间。");
                return;
            }

            sm.addItem(4001018, 1);
            sm.warp(211042300, "sp");
        } else {
            sm.message("目前，这个传送门无法使用。");
        }
    }

    @Script("Zakum04")
    public static void zakum04(ScriptManager sm) {
        // Ali (2030011)
        //   Adobis's Mission I : The Room of Tragedy (280090000)
        if(sm.hasItem(4031061, 1)) {
            sm.sayOk("恭喜你成功通关第一关！好的……我会把你送到#b#p2030008##k所在的位置。在那之前！请注意，你在这里获得的各种特殊物品将无法带出这里。我会从你的物品栏中移除这些物品，请牢记。再见！");
        } else {
            sm.sayOk("他肯定中途退出了。好的，我马上送你离开。在那之前！请注意，你在这里获得的各种特殊物品将无法带出这里。我会从你的物品栏中移除这些物品，请记住这一点。再见！");
        }
        sm.removeItem(4001015);
        sm.removeItem(4001016);
        sm.removeItem(4031061);
        sm.warp(211042300);
    }

    @Script("boxKey0")
    public static void boxKey0(ScriptManager sm) {
        // boxKey0 (2112004)
        //   Adobis's Mission I : Area 9-2 (280010091)
        //   Adobis's Mission I : Area 11-1 (280010110)
        //   Adobis's Mission I : Area 14-1 (280010140)
        //   Adobis's Mission I : Area 16-2 (280011002)
        //   Adobis's Mission I : Area 16-3 (280011003)
        // boxKey0 (2112011)
        //   Adobis's Mission I : Area 4-2 (280010041)
        //   Adobis's Mission I : Area 16-5 (280011005)
        sm.dropRewards(List.of(
                Reward.item(4001016, 1, 1, 1)
        ));
    }

    @Script("Zakum02")
    public static void zakum02(ScriptManager sm) {
        // Lira (2032003)
        //   Adobis's Mission I : Breath of Lava <Level 2> (280020001)
        sm.sayNext("你是如何走过如此艰险的道路来到这里的？太不可思议了！#b#t4031062##k已经到了。请把这个交给我哥哥。你很快就会见到你一直在寻找的人了。");
        if(!sm.canAddItem(4031062, 1)) {
            sm.sayOk("您的ETC库存似乎已满。请腾出空间以便接收物品。");
            return;
        }

        sm.addItem(4031062, 1);
        sm.forceCompleteQuest(100201);
        sm.addExp(15000);
        sm.warp(211042300);
    }

    @Script("Zakum06")
    public static void zakum06(ScriptManager sm) {
        // Amon (2030010)
        //   Adobis's Mission I : Breath of Lava <Level 1> (280020000)
        //   Adobis's Mission I : Breath of Lava <Level 2> (280020001)
        //   Last Mission : akum's Altar (280030000)
        //   Last Mission : Chaos Zakum's Altar (280030001)
        if (sm.getFieldId() == 280030000) {
            boolean exit = false;
            if(sm.getQRValue(QuestRecordType.Zakum).equals("1")) {
                exit = sm.askYesNo("你确定要离开这里吗？你每天最多可以进入扎库姆祭坛两次，如果现在离开，当天余下时间里你只能再次进入这座神殿一次。");
            } else if(sm.getQRValue(QuestRecordType.Zakum).equals("2")) {
                exit = sm.askYesNo("你确定要离开这里吗？你每天最多可以进入扎库姆祭坛两次，既然你已经来过两次了，如果你现在离开，当天余下的时间里将无法再次进入这座神祠。");
            } else {
                sm.sayOk("你怎么能这样？这太疯狂了。快离开这里……");
                exit = true;
            }

            if (exit) {
                sm.partyWarp(211042300, "sp");
            }
        } else {
            if (sm.askYesNo("你确定要退出并离开这里吗？下次再来时，你将不得不从头开始。")) {
                sm.partyWarp(211042300, "sp");
            }
        }
    }

    @Script("Zakum05")
    public static void zakum05(ScriptManager sm) {
        // El Nath : The Door to Zakum (211042300)
        //   ps00 (-722, -217)
        // Dead Mine : The Door to Chaos Zakum (211042301)
        //   ps00 (-722, -217)
        if(!sm.hasQuestCompleted(100202)) {
            sm.sayOk("你只有在通过第三关后才能进入这个地方。同时，你还需要持有火之眼。");
            return;
        }

        sm.setQRValue(QuestRecordType.Zakum, "1");
        sm.playPortalSE();
        sm.partyWarpInstance(ZAKUM_BOSS_MAP, "st00", 211042301, 60 * 60);
    }

    @Script("boss")
    public static void boss(ScriptManager sm) {
        // boss (2111001)
        //   Last Mission : Zakum's Altar (280030000)
        sm.soundEffect("Bgm06/FinalFight");
        sm.broadcastMessage("扎库姆被火之眼的力量召唤出来了。");
        sm.spawnMob(8800000, MobAppearType.SUSPENDED, -11, -215, false, MobType.PARENT_MOB);
        for (int i = 0; i < 8; i++) {
            sm.spawnMob(8800003 + i, MobAppearType.REGEN, -11, -215, false, MobType.SUB_MOB);
        }
    }
}
