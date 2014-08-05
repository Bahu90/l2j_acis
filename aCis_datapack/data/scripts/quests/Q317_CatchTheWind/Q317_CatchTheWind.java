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
package quests.Q317_CatchTheWind;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q317_CatchTheWind extends Quest
{
	private static final String qn = "Q317_CatchTheWind";
	
	// NPC
	private static final int RIZRAELL = 30361;
	
	// Item
	private static final int WIND_SHARD = 1078;
	
	public Q317_CatchTheWind(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			WIND_SHARD
		};
		
		addStartNpc(RIZRAELL);
		addTalkId(RIZRAELL);
		addKillId(20036, 20044);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30361-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30361-08.htm"))
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
				if (player.getLevel() >= 18)
					htmltext = "30361-03.htm";
				else
				{
					htmltext = "30361-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int shards = st.getQuestItemsCount(WIND_SHARD);
				if (shards == 0)
					htmltext = "30361-05.htm";
				else
				{
					int reward = 40 * shards + (shards >= 10 ? 2988 : 0);
					htmltext = "30361-07.htm";
					st.takeItems(WIND_SHARD, -1);
					st.rewardItems(57, reward);
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
		
		if (Rnd.get(100) < 50)
		{
			st.giveItems(WIND_SHARD, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q317_CatchTheWind(317, qn, "Catch the Wind");
	}
}