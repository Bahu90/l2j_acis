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
package quests.Q404_PathToAHumanWizard;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q404_PathToAHumanWizard extends Quest
{
	private static final String qn = "Q404_PathToAHumanWizard";
	
	// Items
	private static final int MapOfLuster = 1280;
	private static final int KeyOfFlame = 1281;
	private static final int FlameEaring = 1282;
	private static final int Mirror = 1283;
	private static final int WindFeather = 1284;
	private static final int WindBangel = 1285;
	private static final int RamasDiary = 1286;
	private static final int SparklePebble = 1287;
	private static final int WaterNecklace = 1288;
	private static final int GoldCoin = 1289;
	private static final int RedSoil = 1290;
	private static final int EarthRing = 1291;
	private static final int BeadOfSeason = 1292;
	
	// NPCs
	private static final int Parina = 30391;
	private static final int EarthSnake = 30409;
	private static final int Lizardman = 30410;
	private static final int FireSalamander = 30411;
	private static final int WindSylph = 30412;
	private static final int WaterUndine = 30413;
	
	public Q404_PathToAHumanWizard(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MapOfLuster,
			KeyOfFlame,
			FlameEaring,
			Mirror,
			WindFeather,
			WindBangel,
			RamasDiary,
			SparklePebble,
			WaterNecklace,
			GoldCoin,
			RedSoil,
			EarthRing
		};
		
		addStartNpc(Parina);
		addTalkId(Parina, EarthSnake, Lizardman, FireSalamander, WindSylph, WaterUndine);
		
		addKillId(20021, 20359, 27030);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30391-08.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30410-03.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(Mirror, 1);
			st.giveItems(WindFeather, 1);
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
		
		int cond = st.getInt("cond");
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getClassId() != ClassId.mage)
				{
					if (player.getClassId() == ClassId.wizard)
						htmltext = "30391-02a.htm";
					else
						htmltext = "30391-01.htm";
					
					st.exitQuest(true);
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30391-02.htm";
					st.exitQuest(true);
				}
				else if (st.hasQuestItems(BeadOfSeason))
				{
					htmltext = "30391-03.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30391-04.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case Parina:
						if (cond >= 1 && cond <= 12)
							htmltext = "30391-05.htm";
						else if (cond == 13)
						{
							htmltext = "30391-06.htm";
							st.takeItems(FlameEaring, 1);
							st.takeItems(WindBangel, 1);
							st.takeItems(WaterNecklace, 1);
							st.takeItems(EarthRing, 1);
							st.giveItems(BeadOfSeason, 1);
							st.rewardExpAndSp(3200, 2020);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case FireSalamander:
						if (cond == 1)
						{
							htmltext = "30411-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(MapOfLuster, 1);
						}
						else if (cond == 2)
							htmltext = "30411-02.htm";
						else if (cond == 3)
						{
							htmltext = "30411-03.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(MapOfLuster, 1);
							st.takeItems(KeyOfFlame, 1);
							st.giveItems(FlameEaring, 1);
						}
						else if (cond >= 4)
							htmltext = "30411-04.htm";
						break;
					
					case WindSylph:
						if (cond == 4)
						{
							htmltext = "30412-01.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(Mirror, 1);
						}
						else if (cond == 5)
							htmltext = "30412-02.htm";
						else if (cond == 6)
						{
							htmltext = "30412-03.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(WindFeather, 1);
							st.giveItems(WindBangel, 1);
						}
						else if (cond >= 7)
							htmltext = "30412-04.htm";
						break;
					
					case Lizardman:
						if (cond == 5)
							htmltext = "30410-01.htm";
						else if (cond >= 6)
							htmltext = "30410-04.htm";
						break;
					
					case WaterUndine:
						if (cond == 7)
						{
							st.set("cond", "8");
							st.giveItems(RamasDiary, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
							htmltext = "30413-01.htm";
						}
						else if (cond == 8)
							htmltext = "30413-02.htm";
						else if (cond == 9)
						{
							htmltext = "30413-03.htm";
							st.set("cond", "10");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(SparklePebble, -1);
							st.takeItems(RamasDiary, 1);
							st.giveItems(WaterNecklace, 1);
						}
						else if (cond >= 10)
							htmltext = "30413-04.htm";
						break;
					
					case EarthSnake:
						if (cond == 10)
						{
							htmltext = "30409-01.htm";
							st.set("cond", "11");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(GoldCoin, 1);
						}
						else if (cond == 11)
							htmltext = "30409-02.htm";
						else if (cond == 12)
						{
							htmltext = "30409-03.htm";
							st.set("cond", "13");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(GoldCoin, 1);
							st.takeItems(RedSoil, 1);
							st.giveItems(EarthRing, 1);
						}
						else if (cond >= 13)
							htmltext = "30409-04.htm";
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
		
		switch (npc.getNpcId())
		{
			case 20359: // Ratman Warrior
				if (st.getInt("cond") == 2 && st.dropItems(KeyOfFlame, 1, 1, 250000))
					st.set("cond", "3");
				break;
			
			case 27030: // Water Seer
				if (st.getInt("cond") == 8 && st.dropItems(SparklePebble, 1, 2, 500000))
					st.set("cond", "9");
				break;
			
			case 20021: // Red Bear
				if (st.getInt("cond") == 11 && st.dropItems(RedSoil, 1, 1, 250000))
					st.set("cond", "12");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q404_PathToAHumanWizard(404, qn, "Path to a Human Wizard");
	}
}