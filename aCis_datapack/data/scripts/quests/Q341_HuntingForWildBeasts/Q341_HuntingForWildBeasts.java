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
package quests.Q341_HuntingForWildBeasts;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q341_HuntingForWildBeasts extends Quest
{
	private static final String qn = "Q341_HuntingForWildBeasts";
	
	// NPC
	private static final int PANO = 30078;
	
	// Item
	private static final int BEAR_SKIN = 4259;
	
	public Q341_HuntingForWildBeasts(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			BEAR_SKIN
		};
		
		addStartNpc(PANO);
		addTalkId(PANO);
		
		// Red bear, brown bear, grizzly, Dion grizzly.
		addKillId(20203, 20021, 20310, 20143);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30078-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
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
				if (player.getLevel() >= 20)
					htmltext = "30078-01.htm";
				else
				{
					htmltext = "30078-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(BEAR_SKIN) >= 20)
				{
					htmltext = "30078-04.htm";
					st.takeItems(BEAR_SKIN, -1);
					st.rewardItems(57, 3710);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				else
					htmltext = "30078-03.htm";
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
		
		st.dropItems(BEAR_SKIN, 1, 20, 400000);
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q341_HuntingForWildBeasts(341, qn, "Hunting for Wild Beasts");
	}
}