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
package quests.Q614_SlayTheEnemyCommander;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q614_SlayTheEnemyCommander extends Quest
{
	private static final String qn = "Q614_SlayTheEnemyCommander";
	
	// Quest Items
	private static final int Tayr_Head = 7241;
	private static final int Wisdom_Feather = 7230;
	private static final int Varka_Alliance_Four = 7224;
	
	public Q614_SlayTheEnemyCommander(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			Tayr_Head
		};
		
		addStartNpc(31377); // Ashas Varka Durai
		addTalkId(31377);
		
		addKillId(25302); // Tayr
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31377-04.htm"))
		{
			if (player.getAllianceWithVarkaKetra() <= -4 && st.getQuestItemsCount(Varka_Alliance_Four) > 0 && st.getQuestItemsCount(Wisdom_Feather) == 0)
			{
				if (player.getLevel() >= 75)
				{
					st.set("cond", "1");
					st.setState(STATE_STARTED);
					st.playSound(QuestState.SOUND_ACCEPT);
				}
				else
				{
					htmltext = "31377-03.htm";
					st.exitQuest(true);
				}
			}
			else
			{
				htmltext = "31377-02.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("31377-07.htm"))
		{
			if (st.getQuestItemsCount(Tayr_Head) == 1)
			{
				st.takeItems(Tayr_Head, -1);
				st.giveItems(Wisdom_Feather, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "31377-06.htm";
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
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
				htmltext = "31377-01.htm";
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(Tayr_Head) > 0)
					htmltext = "31377-05.htm";
				else
					htmltext = "31377-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() <= -4)
			{
				QuestState st = partyMember.getQuestState(qn);
				if (st.hasQuestItems(Varka_Alliance_Four))
				{
					st.set("cond", "2");
					st.giveItems(Tayr_Head, 1);
					st.playSound(QuestState.SOUND_MIDDLE);
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q614_SlayTheEnemyCommander(614, qn, "Slay the enemy commander!");
	}
}