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
package quests.Q338_AlligatorHunter;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q338_AlligatorHunter extends Quest
{
	private static final String qn = "Q338_AlligatorHunter";
	
	// Mob
	private static final int ALLIGATOR = 20135;
	
	// Npc
	private static final int ENVERUN = 30892;
	
	// Item
	private static final int ALLIGATOR_PELTS = 4337;
	
	public Q338_AlligatorHunter(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ALLIGATOR_PELTS
		};
		
		addStartNpc(ENVERUN);
		addTalkId(ENVERUN);
		
		addKillId(ALLIGATOR);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30892-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30892-05.htm"))
		{
			int count = st.getQuestItemsCount(ALLIGATOR_PELTS);
			if (count > 0)
			{
				if (count > 10)
					count = count * 60 + 3430;
				else
					count = count * 60;
				
				st.takeItems(ALLIGATOR_PELTS, -1);
				st.rewardItems(57, count);
			}
			else
				htmltext = "30892-04.htm";
		}
		else if (event.equalsIgnoreCase("30892-08.htm"))
		{
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
			case STATE_CREATED:
				if (player.getLevel() >= 40)
					htmltext = "30892-01.htm";
				else
				{
					htmltext = "30892-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(ALLIGATOR_PELTS) > 0)
					htmltext = "30892-03.htm";
				else
					htmltext = "30892-04.htm";
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
		
		if (Rnd.get(100) < 50)
		{
			st.giveItems(ALLIGATOR_PELTS, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q338_AlligatorHunter(338, qn, "Alligator Hunter");
	}
}