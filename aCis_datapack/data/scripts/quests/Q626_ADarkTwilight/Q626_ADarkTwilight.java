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
package quests.Q626_ADarkTwilight;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q626_ADarkTwilight extends Quest
{
	private static final String qn = "Q626_ADarkTwilight";
	
	// Items
	private static final int BLOOD_OF_SAINT = 7169;
	
	// NPC
	private static final int HIERARCH = 31517;
	
	public Q626_ADarkTwilight(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			BLOOD_OF_SAINT
		};
		
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH);
		
		addKillId(21520, 21523, 21524, 21526, 21529, 21530, 21531, 21532, 21535, 21536, 21539, 21540);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31517-03.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("reward1"))
		{
			if (st.getQuestItemsCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "31517-07.htm";
				st.takeItems(BLOOD_OF_SAINT, 300);
				st.rewardExpAndSp(162773, 12500);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = "31517-08.htm";
		}
		else if (event.equalsIgnoreCase("reward2"))
		{
			if (st.getQuestItemsCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "31517-07.htm";
				st.takeItems(BLOOD_OF_SAINT, 300);
				st.rewardItems(57, 100000);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = "31517-08.htm";
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
				if (player.getLevel() >= 60)
					htmltext = "31517-01.htm";
				else
				{
					htmltext = "31517-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1 && st.getQuestItemsCount(BLOOD_OF_SAINT) < 300)
					htmltext = "31517-05.htm";
				else if (cond == 2)
					htmltext = "31517-04.htm";
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		if (st.dropItemsAlways(BLOOD_OF_SAINT, 1, 300))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q626_ADarkTwilight(626, qn, "A Dark Twilight");
	}
}