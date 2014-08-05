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
package quests.Q328_SenseForBusiness;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q328_SenseForBusiness extends Quest
{
	private static final String qn = "Q328_SenseForBusiness";
	
	// NPC
	private static final int SARIEN = 30436;
	
	// Items
	private static final int MONSTER_EYE_LENS = 1366;
	private static final int MONSTER_EYE_CARCASS = 1347;
	private static final int BASILISK_GIZZARD = 1348;
	
	public Q328_SenseForBusiness(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MONSTER_EYE_LENS,
			MONSTER_EYE_CARCASS,
			BASILISK_GIZZARD
		};
		
		addStartNpc(SARIEN);
		addTalkId(SARIEN);
		
		addKillId(20055, 20059, 20067, 20068, 20070, 20072);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30436-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30436-06.htm"))
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
				if (player.getLevel() >= 21)
					htmltext = "30436-02.htm";
				else
				{
					htmltext = "30436-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int carcasses = st.getQuestItemsCount(MONSTER_EYE_CARCASS);
				int lenses = st.getQuestItemsCount(MONSTER_EYE_LENS);
				int gizzards = st.getQuestItemsCount(BASILISK_GIZZARD);
				int all = carcasses + lenses + gizzards;
				
				if (all == 0)
					htmltext = "30436-04.htm";
				else
				{
					htmltext = "30436-05.htm";
					int reward = (25 * carcasses) + (1000 * lenses) + (60 * gizzards) + (all >= 10 ? 618 : 0);
					st.takeItems(MONSTER_EYE_CARCASS, -1);
					st.takeItems(MONSTER_EYE_LENS, -1);
					st.takeItems(BASILISK_GIZZARD, -1);
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
		
		int chance = Rnd.get(100);
		switch (npc.getNpcId())
		{
			case 20055:
			case 20059:
			case 20067:
			case 20068:
				if (chance < 2)
				{
					st.giveItems(MONSTER_EYE_LENS, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				else if (chance < 35)
				{
					st.giveItems(MONSTER_EYE_CARCASS, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
			
			case 20070:
			case 20072:
				if (chance < 18)
				{
					st.giveItems(BASILISK_GIZZARD, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q328_SenseForBusiness(328, qn, "Sense for Business");
	}
}