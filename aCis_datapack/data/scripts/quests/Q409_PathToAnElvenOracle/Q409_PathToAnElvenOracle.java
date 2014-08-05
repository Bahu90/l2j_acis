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
package quests.Q409_PathToAnElvenOracle;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q409_PathToAnElvenOracle extends Quest
{
	private static final String qn = "Q409_PathToAnElvenOracle";
	
	// Items
	private static final int CrystalMedallion = 1231;
	private static final int SwindlersMoney = 1232;
	private static final int AllanasDiary = 1233;
	private static final int LizardCaptainOrder = 1234;
	private static final int LeafofOracle = 1235;
	private static final int HalfofDiary = 1236;
	private static final int TamilsNecklace = 1275;
	
	// NPCs
	private static final int Manuel = 30293;
	private static final int Allana = 30424;
	private static final int Perrin = 30428;
	
	public Q409_PathToAnElvenOracle(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			CrystalMedallion,
			SwindlersMoney,
			AllanasDiary,
			LizardCaptainOrder,
			HalfofDiary,
			TamilsNecklace
		};
		
		addStartNpc(Manuel);
		addTalkId(Manuel, Allana, Perrin);
		
		addKillId(27032, 27035);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30293-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(CrystalMedallion, 1);
		}
		else if (event.equalsIgnoreCase("spawn_lizards"))
		{
			addSpawn(27032, -92319, 154235, -3284, 2000, false, 0, false);
			addSpawn(27033, -92361, 154190, -3284, 2000, false, 0, false);
			addSpawn(27034, -92375, 154278, -3278, 2000, false, 0, false);
			st.set("cond", "2");
			return null;
		}
		else if (event.equalsIgnoreCase("30428-06.htm"))
			addSpawn(27035, -93194, 147587, -2672, 2000, false, 0, true);
		
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
				if (player.getClassId() != ClassId.elvenMage)
				{
					if (player.getClassId() == ClassId.oracle)
						htmltext = "30293-02a.htm";
					else
						htmltext = "30293-02.htm";
					
					st.exitQuest(true);
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30293-03.htm";
					st.exitQuest(true);
				}
				else if (st.hasQuestItems(LeafofOracle))
				{
					htmltext = "30293-04.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30293-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Manuel:
						if (cond == 1)
							htmltext = "30293-06.htm";
						else if (cond == 2 || cond == 3)
							htmltext = "30293-09.htm";
						else if (cond >= 4 && cond <= 6)
							htmltext = "30293-07.htm";
						else if (cond == 7)
						{
							htmltext = "30293-08.htm";
							st.takeItems(CrystalMedallion, 1);
							st.takeItems(SwindlersMoney, 1);
							st.takeItems(AllanasDiary, 1);
							st.takeItems(LizardCaptainOrder, 1);
							st.giveItems(LeafofOracle, 1);
							st.rewardExpAndSp(3200, 1130);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Allana:
						if (cond == 1)
							htmltext = "30424-01.htm";
						else if (cond == 3)
						{
							htmltext = "30424-02.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(HalfofDiary, 1);
						}
						else if (cond == 4)
							htmltext = "30424-03.htm";
						else if (cond == 5)
							htmltext = "30424-06.htm";
						else if (cond == 6)
						{
							htmltext = "30424-04.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(HalfofDiary, -1);
							st.giveItems(AllanasDiary, 1);
						}
						else if (cond == 7)
							htmltext = "30424-05.htm";
						break;
					
					case Perrin:
						if (cond == 4)
							htmltext = "30428-01.htm";
						else if (cond == 5)
						{
							htmltext = "30428-04.htm";
							st.set("cond", "6");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(TamilsNecklace, -1);
							st.giveItems(SwindlersMoney, 1);
						}
						else if (cond > 5)
							htmltext = "30428-05.htm";
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
		
		if (npc.getNpcId() == 27032)
		{
			if (st.getInt("cond") == 2)
			{
				st.set("cond", "3");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.giveItems(LizardCaptainOrder, 1);
			}
		}
		else
		{
			if (st.getInt("cond") == 4)
			{
				st.set("cond", "5");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.giveItems(TamilsNecklace, 1);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q409_PathToAnElvenOracle(409, qn, "Path to an Elven Oracle");
	}
}