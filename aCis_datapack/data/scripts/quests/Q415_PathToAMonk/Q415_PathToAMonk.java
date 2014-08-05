/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q415_PathToAMonk;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.templates.item.L2WeaponType;

public class Q415_PathToAMonk extends Quest
{
	private static final String qn = "Q415_PathToAMonk";
	
	// Items
	private static final int Pomegranate = 1593;
	private static final int LeatherPouch1 = 1594;
	private static final int LeatherPouch2 = 1595;
	private static final int LeatherPouch3 = 1596;
	private static final int LeatherPouchFull1 = 1597;
	private static final int LeatherPouchFull2 = 1598;
	private static final int LeatherPouchFull3 = 1599;
	private static final int KashaBearClaw = 1600;
	private static final int KashaBladeSpiderTalon = 1601;
	private static final int ScarletSalamanderScale = 1602;
	private static final int FierySpiritScroll = 1603;
	private static final int RosheeksLetter = 1604;
	private static final int GantakisRecommendation = 1605;
	private static final int Fig = 1606;
	private static final int LeatherPouch4 = 1607;
	private static final int LeatherPouchFull4 = 1608;
	private static final int VukuOrcTusk = 1609;
	private static final int RatmanFang = 1610;
	private static final int LangkLizardmanTeeth = 1611;
	private static final int FelimLizardmanTeeth = 1612;
	private static final int IronWillScroll = 1613;
	private static final int TorukusLetter = 1614;
	private static final int KhavatariTotem = 1615;
	private static final int KashaSpidersTeeth = 8545;
	private static final int HornOfBaarDreVanul = 8546;
	
	// NPCs
	private static final int Gantaki = 30587;
	private static final int Rosheek = 30590;
	private static final int Kasman = 30501;
	private static final int Toruku = 30591;
	private static final int Aren = 32056;
	private static final int Moira = 31979;
	
	public Q415_PathToAMonk(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			Pomegranate,
			LeatherPouch1,
			LeatherPouch2,
			LeatherPouch3,
			LeatherPouchFull1,
			LeatherPouchFull2,
			LeatherPouchFull3,
			KashaBearClaw,
			KashaBladeSpiderTalon,
			ScarletSalamanderScale,
			FierySpiritScroll,
			RosheeksLetter,
			GantakisRecommendation,
			Fig,
			LeatherPouch4,
			LeatherPouchFull4,
			VukuOrcTusk,
			RatmanFang,
			LangkLizardmanTeeth,
			FelimLizardmanTeeth,
			IronWillScroll,
			TorukusLetter,
			KashaSpidersTeeth,
			HornOfBaarDreVanul
		};
		
		addStartNpc(Gantaki);
		addTalkId(Gantaki, Rosheek, Kasman, Toruku, Aren, Moira);
		
		addKillId(20014, 20017, 20024, 20359, 20415, 20476, 20478, 20479, 21118);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30587-05.htm"))
		{
			if (player.getClassId() != ClassId.orcFighter)
			{
				if (player.getClassId() == ClassId.orcMonk)
					htmltext = "30587-02a.htm";
				else
					htmltext = "30587-02.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30587-03.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(KhavatariTotem))
			{
				htmltext = "30587-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30587-06.htm"))
		{
			st.set("cond", "1");
			st.setState(Quest.STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(Pomegranate, 1);
		}
		else if (event.equalsIgnoreCase("30587-09a.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(RosheeksLetter, 1);
			st.giveItems(GantakisRecommendation, 1);
		}
		else if (event.equalsIgnoreCase("30587-09b.htm"))
		{
			st.set("cond", "14");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(RosheeksLetter, 1);
		}
		else if (event.equalsIgnoreCase("32056-03.htm"))
		{
			st.set("cond", "15");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32056-08.htm"))
		{
			st.set("cond", "20");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31979-03.htm"))
		{
			st.takeItems(FierySpiritScroll, 1);
			st.giveItems(KhavatariTotem, 1);
			st.rewardExpAndSp(3200, 4230);
			player.broadcastPacket(new SocialAction(player, 3));
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case Quest.STATE_CREATED:
				htmltext = "30587-01.htm";
				break;
			
			case Quest.STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Gantaki:
						if (cond == 1)
							htmltext = "30587-07.htm";
						else if (cond >= 2 && cond <= 7)
							htmltext = "30587-08.htm";
						else if (cond == 8)
							htmltext = "30587-09.htm";
						else if (cond >= 10)
							htmltext = "30587-11.htm";
						break;
					
					case Rosheek:
						if (cond == 1)
						{
							htmltext = "30590-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(Pomegranate, 1);
							st.giveItems(LeatherPouch1, 1);
						}
						else if (cond == 2)
							htmltext = "30590-02.htm";
						else if (cond == 3)
						{
							htmltext = "30590-03.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LeatherPouchFull1, 1);
							st.giveItems(LeatherPouch2, 1);
						}
						else if (cond == 4)
							htmltext = "30590-04.htm";
						else if (cond == 5)
						{
							htmltext = "30590-05.htm";
							st.set("cond", "6");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LeatherPouchFull2, 1);
							st.giveItems(LeatherPouch3, 1);
						}
						else if (cond == 6)
							htmltext = "30590-06.htm";
						else if (cond == 7)
						{
							htmltext = "30590-07.htm";
							st.set("cond", "8");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LeatherPouchFull3, 1);
							st.giveItems(FierySpiritScroll, 1);
							st.giveItems(RosheeksLetter, 1);
						}
						else if (cond == 8)
							htmltext = "30590-08.htm";
						else if (cond >= 9)
							htmltext = "30590-09.htm";
						break;
					
					case Kasman:
						if (cond == 9)
						{
							htmltext = "30501-01.htm";
							st.set("cond", "10");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(GantakisRecommendation, 1);
							st.giveItems(Fig, 1);
						}
						else if (cond == 10)
							htmltext = "30501-02.htm";
						else if (cond == 11 || cond == 12)
							htmltext = "30501-03.htm";
						else if (cond == 13)
						{
							htmltext = "30501-04.htm";
							st.takeItems(FierySpiritScroll, 1);
							st.takeItems(IronWillScroll, 1);
							st.takeItems(TorukusLetter, 1);
							st.giveItems(KhavatariTotem, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Toruku:
						if (cond == 10)
						{
							htmltext = "30591-01.htm";
							st.set("cond", "11");;
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(Fig, 1);
							st.giveItems(LeatherPouch4, 1);
						}
						else if (cond == 11)
							htmltext = "30591-02.htm";
						else if (cond == 12)
						{
							htmltext = "30591-03.htm";
							st.set("cond", "13");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LeatherPouchFull4, 1);
							st.giveItems(IronWillScroll, 1);
							st.giveItems(TorukusLetter, 1);
						}
						else if (cond == 13)
							htmltext = "30591-04.htm";
						break;
					
					case Aren:
						if (cond == 14)
							htmltext = "32056-01.htm";
						else if (cond == 15)
							htmltext = "32056-04.htm";
						else if (cond == 16)
						{
							htmltext = "32056-05.htm";
							st.set("cond", "17");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(KashaSpidersTeeth, -1);
						}
						else if (cond == 17)
							htmltext = "32056-06.htm";
						else if (cond == 18)
						{
							htmltext = "32056-07.htm";
							st.set("cond", "19");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(HornOfBaarDreVanul, -1);
						}
						else if (cond == 20)
							htmltext = "32056-09.htm";
						break;
					
					case Moira:
						if (cond == 20)
							htmltext = "31979-01.htm";
						break;
				
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		final L2WeaponType weapon = player.getActiveWeaponItem().getItemType();
		if (!weapon.equals(L2WeaponType.DUALFIST) && !weapon.equals(L2WeaponType.FIST))
		{
			st.playSound(QuestState.SOUND_GIVEUP);
			st.exitQuest(true);
			return null;
		}
		
		switch (npc.getNpcId())
		{
			case 20479:
				if (st.getInt("cond") == 2 && st.dropItemsAlways(KashaBearClaw, 1, 5))
				{
					st.takeItems(KashaBearClaw, -1);
					st.takeItems(LeatherPouch1, 1);
					st.giveItems(LeatherPouchFull1, 1);
					st.set("cond", "3");
				}
				break;
			
			case 20478:
				if (st.getInt("cond") == 4 && st.dropItemsAlways(KashaBladeSpiderTalon, 1, 5))
				{
					st.takeItems(KashaBladeSpiderTalon, -1);
					st.takeItems(LeatherPouch2, 1);
					st.giveItems(LeatherPouchFull2, 1);
					st.set("cond", "5");
				}
				else if (st.getInt("cond") == 15 && st.dropItems(KashaSpidersTeeth, 1, 6, 500000))
					st.set("cond", "16");
				break;
			
			case 20476:
				if (st.getInt("cond") == 15 && st.dropItems(KashaSpidersTeeth, 1, 6, 500000))
					st.set("cond", "16");
				break;
			
			case 20415:
				if (st.getInt("cond") == 6 && st.dropItemsAlways(ScarletSalamanderScale, 1, 5))
				{
					st.takeItems(ScarletSalamanderScale, -1);
					st.takeItems(LeatherPouch3, 1);
					st.giveItems(LeatherPouchFull3, 1);
					st.set("cond", "7");
				}
				break;
			
			case 20014:
				if (st.getInt("cond") == 11 && st.dropItemsAlways(FelimLizardmanTeeth, 1, 3))
				{
					if (st.getQuestItemsCount(RatmanFang) == 3 && st.getQuestItemsCount(LangkLizardmanTeeth) == 3 && st.getQuestItemsCount(VukuOrcTusk) == 3)
					{
						st.takeItems(VukuOrcTusk, -1);
						st.takeItems(RatmanFang, -1);
						st.takeItems(LangkLizardmanTeeth, -1);
						st.takeItems(FelimLizardmanTeeth, -1);
						st.takeItems(LeatherPouch4, 1);
						st.giveItems(LeatherPouchFull4, 1);
						st.set("cond", "12");
					}
				}
				break;
			
			case 20017:
				if (st.getInt("cond") == 11 && st.dropItemsAlways(VukuOrcTusk, 1, 3))
				{
					if (st.getQuestItemsCount(RatmanFang) == 3 && st.getQuestItemsCount(LangkLizardmanTeeth) == 3 && st.getQuestItemsCount(FelimLizardmanTeeth) == 3)
					{
						st.takeItems(VukuOrcTusk, -1);
						st.takeItems(RatmanFang, -1);
						st.takeItems(LangkLizardmanTeeth, -1);
						st.takeItems(FelimLizardmanTeeth, -1);
						st.takeItems(LeatherPouch4, 1);
						st.giveItems(LeatherPouchFull4, 1);
						st.set("cond", "12");
					}
				}
				break;
			
			case 20024:
				if (st.getInt("cond") == 11 && st.dropItemsAlways(LangkLizardmanTeeth, 1, 3))
				{
					if (st.getQuestItemsCount(RatmanFang) == 3 && st.getQuestItemsCount(FelimLizardmanTeeth) == 3 && st.getQuestItemsCount(VukuOrcTusk) == 3)
					{
						st.takeItems(VukuOrcTusk, -1);
						st.takeItems(RatmanFang, -1);
						st.takeItems(LangkLizardmanTeeth, -1);
						st.takeItems(FelimLizardmanTeeth, -1);
						st.takeItems(LeatherPouch4, 1);
						st.giveItems(LeatherPouchFull4, 1);
						st.set("cond", "12");
					}
				}
				break;
			
			case 20359:
				if (st.getInt("cond") == 11 && st.dropItemsAlways(RatmanFang, 1, 3))
				{
					if (st.getQuestItemsCount(LangkLizardmanTeeth) == 3 && st.getQuestItemsCount(FelimLizardmanTeeth) == 3 && st.getQuestItemsCount(VukuOrcTusk) == 3)
					{
						st.takeItems(VukuOrcTusk, -1);
						st.takeItems(RatmanFang, -1);
						st.takeItems(LangkLizardmanTeeth, -1);
						st.takeItems(FelimLizardmanTeeth, -1);
						st.takeItems(LeatherPouch4, 1);
						st.giveItems(LeatherPouchFull4, 1);
						st.set("cond", "12");
					}
				}
				break;
			
			case 21118:
				if (st.getInt("cond") == 17)
				{
					st.set("cond", "18");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.giveItems(HornOfBaarDreVanul, 1);
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q415_PathToAMonk(415, qn, "Path to a Monk");
	}
}