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
package quests.Q258_BringWolfPelts;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q258_BringWolfPelts extends Quest
{
	private static final String qn = "Q258_BringWolfPelts";
	
	// NPC
	private static final int LECTOR = 30001;
	
	// Monsters
	private static final int WOLF = 20120;
	private static final int ELDER_WOLF = 20442;
	
	// Item
	private static final int WOLF_PELT = 702;
	
	// Rewards
	private static final int Cotton_Shirt = 390;
	private static final int Leather_Pants = 29;
	private static final int Leather_Shirt = 22;
	private static final int Short_Leather_Gloves = 1119;
	private static final int Tunic = 426;
	
	public Q258_BringWolfPelts(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			WOLF_PELT
		};
		
		addStartNpc(LECTOR);
		addTalkId(LECTOR);
		
		addKillId(WOLF, ELDER_WOLF);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30001-03.htm"))
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
				htmltext = (player.getLevel() < 3) ? "30001-01.htm" : "30001-02.htm";
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(WOLF_PELT) < 40)
					htmltext = "30001-05.htm";
				else
				{
					st.takeItems(WOLF_PELT, -1);
					int randomNumber = Rnd.get(16);
					
					// Reward is based on a random number (1D16).
					if (randomNumber == 0)
						st.giveItems(Cotton_Shirt, 1);
					else if (randomNumber < 6)
						st.giveItems(Leather_Pants, 1);
					else if (randomNumber < 9)
						st.giveItems(Leather_Shirt, 1);
					else if (randomNumber < 13)
						st.giveItems(Short_Leather_Gloves, 1);
					else
						st.giveItems(Tunic, 1);
					
					htmltext = "30001-06.htm";
					
					if (randomNumber == 0)
						st.playSound(QuestState.SOUND_JACKPOT);
					else
						st.playSound(QuestState.SOUND_FINISH);
					
					st.exitQuest(true);
				}
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
		
		if (st.dropItems(WOLF_PELT, 1, 40, 400000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q258_BringWolfPelts(258, qn, "Bring Wolf Pelts");
	}
}