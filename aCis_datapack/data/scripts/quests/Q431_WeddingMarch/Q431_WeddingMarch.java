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
package quests.Q431_WeddingMarch;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q431_WeddingMarch extends Quest
{
	private static final String qn = "Q431_WeddingMarch";
	
	// NPC
	private static final int KANTABILON = 31042;
	
	// Item
	private static final int SILVER_CRYSTAL = 7540;
	
	// Reward
	private static final int WEDDING_ECHO_CRYSTAL = 7062;
	
	public Q431_WeddingMarch(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SILVER_CRYSTAL
		};
		
		addStartNpc(KANTABILON);
		addTalkId(KANTABILON);
		
		addKillId(20786, 20787);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31042-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31042-05.htm"))
		{
			if (st.getQuestItemsCount(SILVER_CRYSTAL) < 50)
				htmltext = "31042-03.htm";
			else
			{
				st.takeItems(SILVER_CRYSTAL, -1);
				st.giveItems(WEDDING_ECHO_CRYSTAL, 25);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
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
				if (player.getLevel() >= 38)
					htmltext = "31042-01.htm";
				else
				{
					htmltext = "31042-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "31042-02.htm";
				else if (cond == 2)
				{
					if (st.getQuestItemsCount(SILVER_CRYSTAL) < 50)
						htmltext = "31042-03.htm";
					else
						htmltext = "31042-04.htm";
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
		
		if (st.dropItemsAlways(SILVER_CRYSTAL, 1, 50))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q431_WeddingMarch(431, qn, "Wedding March");
	}
}