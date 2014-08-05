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
package quests.Q292_BrigandsSweep;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q292_BrigandsSweep extends Quest
{
	private static final String qn = "Q292_BrigandsSweep";
	
	// NPCs
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;
	
	// Items
	private static final int GOBLIN_NECKLACE = 1483;
	private static final int GOBLIN_PENDANT = 1484;
	private static final int GOBLIN_LORD_PENDANT = 1485;
	private static final int SUSPICIOUS_MEMO = 1486;
	private static final int SUSPICIOUS_CONTRACT = 1487;
	
	// Monsters
	private static final int GOBLIN_BRIGAND = 20322;
	private static final int GOBLIN_BRIGAND_LEADER = 20323;
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 20324;
	private static final int GOBLIN_SNOOPER = 20327;
	private static final int GOBLIN_LORD = 20528;
	
	public Q292_BrigandsSweep(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			GOBLIN_NECKLACE,
			GOBLIN_PENDANT,
			GOBLIN_LORD_PENDANT,
			SUSPICIOUS_MEMO,
			SUSPICIOUS_CONTRACT
		};
		
		addStartNpc(SPIRON);
		addTalkId(SPIRON, BALANKI);
		
		addKillId(GOBLIN_BRIGAND, GOBLIN_BRIGAND_LEADER, GOBLIN_BRIGAND_LIEUTENANT, GOBLIN_SNOOPER, GOBLIN_LORD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30532-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30532-06.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getRace() != Race.Dwarf)
					htmltext = "30532-00.htm";
				else if (player.getLevel() < 5)
					htmltext = "30532-01.htm";
				else
					htmltext = "30532-02.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case SPIRON:
						int GNC = st.getQuestItemsCount(GOBLIN_NECKLACE);
						int GPC = st.getQuestItemsCount(GOBLIN_PENDANT);
						int GLPC = st.getQuestItemsCount(GOBLIN_LORD_PENDANT);
						int SM = st.getQuestItemsCount(SUSPICIOUS_MEMO);
						int SC = st.getQuestItemsCount(SUSPICIOUS_CONTRACT);
						
						int COUNT_ALL = GNC + GPC + GLPC + SC;
						
						if (COUNT_ALL == 0)
							htmltext = "30532-04.htm";
						else
						{
							if (SC > 0)
								htmltext = "30532-10.htm";
							else if (SM > 0)
							{
								if (SM > 1)
									htmltext = "30532-09.htm";
								else
									htmltext = "30532-08.htm";
							}
							else
								htmltext = "30532-05.htm";
							
							int reward = (12 * GNC) + (36 * GPC) + (33 * GLPC) + (COUNT_ALL >= 10 ? 1000 : 0) + (SC == 1 ? 1120 : 0);
							
							st.takeItems(GOBLIN_NECKLACE, -1);
							st.takeItems(GOBLIN_PENDANT, -1);
							st.takeItems(GOBLIN_LORD_PENDANT, -1);
							st.takeItems(SUSPICIOUS_CONTRACT, -1);
							st.rewardItems(57, reward);
						}
						break;
					
					case BALANKI:
						if (st.hasQuestItems(SUSPICIOUS_CONTRACT))
						{
							htmltext = "30533-02.htm";
							st.set("cond", "1");
							st.takeItems(SUSPICIOUS_CONTRACT, -1);
							st.rewardItems(57, 1500);
						}
						else
							htmltext = "30533-01.htm";
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
			case GOBLIN_BRIGAND:
			case GOBLIN_SNOOPER:
				st.dropItems(GOBLIN_NECKLACE, 1, 0, 500000);
				break;
			
			case GOBLIN_BRIGAND_LEADER:
			case GOBLIN_BRIGAND_LIEUTENANT:
				st.dropItems(GOBLIN_PENDANT, 1, 0, 400000);
				break;
			
			case GOBLIN_LORD:
				st.dropItems(GOBLIN_LORD_PENDANT, 1, 0, 300000);
				break;
		}
		
		if (st.getInt("cond") == 1 && st.dropItems(SUSPICIOUS_MEMO, 1, 3, 100000))
		{
			st.set("cond", "2");
			st.takeItems(SUSPICIOUS_MEMO, -1);
			st.giveItems(SUSPICIOUS_CONTRACT, 1);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q292_BrigandsSweep(292, qn, "Brigands Sweep");
	}
}