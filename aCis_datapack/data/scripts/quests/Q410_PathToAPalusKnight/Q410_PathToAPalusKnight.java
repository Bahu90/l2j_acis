/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q410_PathToAPalusKnight;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q410_PathToAPalusKnight extends Quest
{
	private static final String qn = "Q410_PathToAPalusKnight";
	
	// Items
	private static final int PalusTalisman = 1237;
	private static final int LycanthropeSkull = 1238;
	private static final int VirgilsLetter = 1239;
	private static final int MorteTalisman = 1240;
	private static final int PredatorCarapace = 1241;
	private static final int TrimdenSilk = 1242;
	private static final int CoffinOfEternalRest = 1243;
	private static final int GazeOfAbyss = 1244;
	
	// NPCs
	private static final int Kalinta = 30422;
	private static final int Virgil = 30329;
	
	// Monsters
	private static final int PoisonSpider = 20038;
	private static final int ArachnidTracker = 20043;
	private static final int Lycan = 20049;
	
	public Q410_PathToAPalusKnight(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			PalusTalisman,
			LycanthropeSkull,
			VirgilsLetter,
			MorteTalisman,
			PredatorCarapace,
			TrimdenSilk,
			CoffinOfEternalRest
		};
		
		addStartNpc(Virgil);
		addTalkId(Virgil, Kalinta);
		
		addKillId(PoisonSpider, ArachnidTracker, Lycan);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30329-05.htm"))
		{
			if (player.getClassId() != ClassId.darkFighter)
			{
				if (player.getClassId() == ClassId.palusKnight)
					htmltext = "30329-02a.htm";
				else
					htmltext = "30329-03.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30329-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(GazeOfAbyss))
			{
				htmltext = "30329-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30329-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(PalusTalisman, 1);
		}
		else if (event.equalsIgnoreCase("30329-10.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(VirgilsLetter, 1);
		}
		else if (event.equalsIgnoreCase("30422-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(VirgilsLetter, 1);
			st.giveItems(MorteTalisman, 1);
		}
		else if (event.equalsIgnoreCase("30422-06.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(MorteTalisman, 1);
			st.takeItems(TrimdenSilk, -1);
			st.takeItems(PredatorCarapace, -1);
			st.giveItems(CoffinOfEternalRest, 1);
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
			case STATE_CREATED:
				htmltext = "30329-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Virgil:
						if (cond == 1)
						{
							if (!st.hasQuestItems(LycanthropeSkull))
								htmltext = "30329-07.htm";
							else
								htmltext = "30329-08.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30329-09.htm";
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LycanthropeSkull, -1);
							st.takeItems(PalusTalisman, 1);
						}
						else if (cond >= 3 && cond <= 5)
							htmltext = "30329-12.htm";
						else if (cond == 6)
						{
							htmltext = "30329-11.htm";
							st.takeItems(CoffinOfEternalRest, 1);
							st.giveItems(GazeOfAbyss, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Kalinta:
						if (cond == 3)
							htmltext = "30422-01.htm";
						else if (cond == 4)
						{
							if (!st.hasQuestItems(TrimdenSilk) || !st.hasQuestItems(PredatorCarapace))
								htmltext = "30422-03.htm";
							else
								htmltext = "30422-04.htm";
						}
						else if (cond == 5)
							htmltext = "30422-05.htm";
						else if (cond == 6)
							htmltext = "30422-06.htm";
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
			case Lycan:
				if (st.getInt("cond") == 1 && st.dropItemsAlways(LycanthropeSkull, 1, 13))
					st.set("cond", "2");
				break;
			
			case ArachnidTracker:
				if (st.getInt("cond") == 4 && st.dropItemsAlways(TrimdenSilk, 1, 5) && st.hasQuestItems(PredatorCarapace))
					st.set("cond", "5");
				break;
			
			case PoisonSpider:
				if (st.getInt("cond") == 4 && st.dropItems(PredatorCarapace, 1, 1, 500000) && st.getQuestItemsCount(TrimdenSilk) == 5)
					st.set("cond", "5");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q410_PathToAPalusKnight(410, qn, "Path to a Palus Knight");
	}
}