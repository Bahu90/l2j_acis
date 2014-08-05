/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q607_ProveYourCourage;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q607_ProveYourCourage extends Quest
{
	private static final String qn = "Q607_ProveYourCourage";
	
	// Items
	private static final int Shadith_Head = 7235;
	private static final int Valor_Totem = 7219;
	private static final int Ketra_Alliance_Three = 7213;
	
	public Q607_ProveYourCourage(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			Shadith_Head
		};
		
		addStartNpc(31370); // Kadun Zu Ketra
		addTalkId(31370);
		
		addKillId(25309); // Shadith
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31370-04.htm"))
		{
			if (player.getAllianceWithVarkaKetra() >= 3 && st.getQuestItemsCount(Ketra_Alliance_Three) > 0 && st.getQuestItemsCount(Valor_Totem) == 0)
			{
				if (player.getLevel() >= 75)
				{
					st.set("cond", "1");
					st.setState(STATE_STARTED);
					st.playSound(QuestState.SOUND_ACCEPT);
				}
				else
				{
					htmltext = "31370-03.htm";
					st.exitQuest(true);
				}
			}
			else
			{
				htmltext = "31370-02.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("31370-07.htm"))
		{
			if (st.getQuestItemsCount(Shadith_Head) == 1)
			{
				st.takeItems(Shadith_Head, -1);
				st.giveItems(Valor_Totem, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "31370-06.htm";
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
				htmltext = "31370-01.htm";
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(Shadith_Head) == 1)
					htmltext = "31370-05.htm";
				else
					htmltext = "31370-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() >= 3)
			{
				QuestState st = partyMember.getQuestState(qn);
				if (st.hasQuestItems(Ketra_Alliance_Three))
				{
					st.set("cond", "2");
					st.giveItems(Shadith_Head, 1);
					st.playSound(QuestState.SOUND_MIDDLE);
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q607_ProveYourCourage(607, qn, "Prove your courage!");
	}
}