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
package quests.Q631_DeliciousTopChoiceMeat;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.util.Util;

public class Q631_DeliciousTopChoiceMeat extends Quest
{
	private static final String qn = "Q631_DeliciousTopChoiceMeat";
	
	// NPC
	private static final int TUNATUN = 31537;
	
	// Item
	private static final int TOP_QUALITY_MEAT = 7546;
	
	// Rewards
	private static final int[][] rewards =
	{
		{
			4039,
			15
		},
		{
			4043,
			15
		},
		{
			4044,
			15
		},
		{
			4040,
			10
		},
		{
			4042,
			10
		},
		{
			4041,
			5
		}
	};
	
	public Q631_DeliciousTopChoiceMeat(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			TOP_QUALITY_MEAT
		};
		
		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
		
		for (int num1 = 21460; num1 <= 21468; num1++)
			addKillId(num1);
		
		for (int num2 = 21479; num2 <= 21487; num2++)
			addKillId(num2);
		
		for (int num3 = 21498; num3 <= 21506; num3++)
			addKillId(num3);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31537-03.htm"))
		{
			if (player.getLevel() >= 65)
			{
				st.setState(STATE_STARTED);
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
			}
			else
			{
				htmltext = "31537-02.htm";
				st.exitQuest(true);
			}
		}
		else if (Util.isDigit(event))
		{
			if (st.getQuestItemsCount(TOP_QUALITY_MEAT) >= 120)
			{
				htmltext = "31537-06.htm";
				st.takeItems(TOP_QUALITY_MEAT, -1);
				
				int[] reward = rewards[Integer.parseInt(event)];
				st.rewardItems(reward[0], reward[1]);
				
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				htmltext = "31537-07.htm";
			}
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
				htmltext = "31537-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "31537-03a.htm";
				else if (cond == 2)
				{
					if (st.getQuestItemsCount(TOP_QUALITY_MEAT) >= 120)
						htmltext = "31537-04.htm";
					else
					{
						st.set("cond", "1");
						htmltext = "31537-03a.htm";
					}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
			return null;
		
		QuestState st = partyMember.getQuestState(qn);
		
		if (st.dropItemsAlways(TOP_QUALITY_MEAT, 1, 120))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q631_DeliciousTopChoiceMeat(631, qn, "Delicious Top Choice Meat");
	}
}