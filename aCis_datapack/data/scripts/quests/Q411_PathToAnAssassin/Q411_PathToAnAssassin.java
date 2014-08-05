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
package quests.Q411_PathToAnAssassin;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q411_PathToAnAssassin extends Quest
{
	private static final String qn = "Q411_PathToAnAssassin";
	
	// Items
	private static final int ShilensCall = 1245;
	private static final int ArkeniasLetter = 1246;
	private static final int LeikansNote = 1247;
	private static final int Molars = 1248;
	private static final int ShilenTears = 1250;
	private static final int ArkeniasReccomend = 1251;
	private static final int IronHeart = 1252;
	
	// NPCs
	private static final int Triskel = 30416;
	private static final int Arkenia = 30419;
	private static final int Leikan = 30382;
	
	public Q411_PathToAnAssassin(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ShilensCall,
			ArkeniasLetter,
			LeikansNote,
			Molars,
			ShilenTears,
			ArkeniasReccomend
		};
		
		addStartNpc(Triskel);
		addTalkId(Triskel, Arkenia, Leikan);
		
		addKillId(27036, 20369);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30416-05.htm"))
		{
			if (player.getClassId() != ClassId.darkFighter)
			{
				if (player.getClassId() == ClassId.assassin)
					htmltext = "30416-02a.htm";
				else
					htmltext = "30416-02.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30416-03.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(IronHeart))
			{
				htmltext = "30416-04.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
				st.giveItems(ShilensCall, 1);
			}
		}
		else if (event.equalsIgnoreCase("30419-05.htm"))
		{
			st.set("cond", "2");
			st.takeItems(ShilensCall, 1);
			st.giveItems(ArkeniasLetter, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30382-03.htm"))
		{
			st.set("cond", "3");
			st.takeItems(ArkeniasLetter, 1);
			st.giveItems(LeikansNote, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
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
				htmltext = "30416-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Triskel:
						if (cond == 1)
							htmltext = "30416-11.htm";
						else if (cond == 2)
							htmltext = "30416-07.htm";
						else if (cond == 3 || cond == 4)
							htmltext = "30416-08.htm";
						else if (cond == 5)
							htmltext = "30416-09.htm";
						else if (cond == 6)
							htmltext = "30416-10.htm";
						else if (cond == 7)
						{
							htmltext = "30416-06.htm";
							st.takeItems(ArkeniasReccomend, 1);
							st.giveItems(IronHeart, 1);
							st.rewardExpAndSp(3200, 3930);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Arkenia:
						if (cond == 1)
							htmltext = "30419-01.htm";
						else if (cond == 2)
							htmltext = "30419-07.htm";
						else if (cond == 3 || cond == 4)
							htmltext = "30419-10.htm";
						else if (cond == 5)
							htmltext = "30419-11.htm";
						else if (cond == 6)
						{
							htmltext = "30419-08.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ShilenTears, -1);
							st.giveItems(ArkeniasReccomend, 1);
						}
						else if (cond == 7)
							htmltext = "30419-09.htm";
						break;
					
					case Leikan:
						if (cond == 2)
							htmltext = "30382-01.htm";
						else if (cond == 3)
						{
							if (!st.hasQuestItems(Molars))
								htmltext = "30382-05.htm";
							else
								htmltext = "30382-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30382-07.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(Molars, -1);
							st.takeItems(LeikansNote, -1);
						}
						else if (cond == 5)
							htmltext = "30382-09.htm";
						else if (cond >= 6)
							htmltext = "30382-08.htm";
						break;
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
		
		if (npc.getNpcId() == 20369)
		{
			if (st.getInt("cond") == 3 && st.dropItemsAlways(Molars, 1, 10))
				st.set("cond", "4");
		}
		else
		{
			if (st.getInt("cond") == 5 && st.dropItemsAlways(ShilenTears, 1, 1))
				st.set("cond", "6");
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q411_PathToAnAssassin(411, qn, "Path to an Assassin");
	}
}