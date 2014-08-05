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
package quests.Q326_VanquishRemnants;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q326_VanquishRemnants extends Quest
{
	private static final String qn = "Q326_VanquishRemnants";
	
	// Items
	private static final int RED_CROSS_BADGE = 1359;
	private static final int BLUE_CROSS_BADGE = 1360;
	private static final int BLACK_CROSS_BADGE = 1361;
	
	// Reward
	private static final int BLACK_LION_MARK = 1369;
	
	public Q326_VanquishRemnants(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			RED_CROSS_BADGE,
			BLUE_CROSS_BADGE,
			BLACK_CROSS_BADGE
		};
		
		addStartNpc(30435); // Leopold
		addTalkId(30435);
		
		addKillId(20053, 20437, 20058, 20436, 20061, 20439, 20063, 20066, 20438);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30435-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30435-07.htm"))
		{
			st.playSound(QuestState.SOUND_GIVEUP);
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
				if (player.getLevel() >= 21)
					htmltext = "30435-02.htm";
				else
				{
					htmltext = "30435-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int redBadges = st.getQuestItemsCount(RED_CROSS_BADGE);
				int blueBadges = st.getQuestItemsCount(BLUE_CROSS_BADGE);
				int blackBadges = st.getQuestItemsCount(BLACK_CROSS_BADGE);
				
				int badgesSum = redBadges + blueBadges + blackBadges;
				
				if (badgesSum > 0)
				{
					st.takeItems(RED_CROSS_BADGE, -1);
					st.takeItems(BLUE_CROSS_BADGE, -1);
					st.takeItems(BLACK_CROSS_BADGE, -1);
					st.rewardItems(57, ((redBadges * 46) + (blueBadges * 52) + (blackBadges * 58) + ((badgesSum >= 10) ? 4320 : 0)));
					
					if (badgesSum >= 100)
					{
						if (!st.hasQuestItems(BLACK_LION_MARK))
						{
							htmltext = "30435-06.htm";
							st.giveItems(BLACK_LION_MARK, 1);
							st.playSound(QuestState.SOUND_ITEMGET);
						}
						else
							htmltext = "30435-09.htm";
					}
					else
						htmltext = "30435-05.htm";
				}
				else
					htmltext = "30435-04.htm";
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
			case 20053:
			case 20437:
			case 20058:
				if (chance <= 33)
				{
					st.giveItems(RED_CROSS_BADGE, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
			
			case 20436:
			case 20061:
			case 20439:
			case 20063:
				if (chance <= 16)
				{
					st.giveItems(BLUE_CROSS_BADGE, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
			
			case 20066:
			case 20438:
				if (chance <= 12)
				{
					st.giveItems(BLACK_CROSS_BADGE, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q326_VanquishRemnants(326, qn, "Vanquish Remnants");
	}
}