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
package quests.Q407_PathToAnElvenScout;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q407_PathToAnElvenScout extends Quest
{
	private static final String qn = "Q407_PathToAnElvenScout";
	
	// Items
	private static final int ReisasLetter = 1207;
	private static final int PriasLetter1 = 1208;
	private static final int PriasLetter2 = 1209;
	private static final int PriasLetter3 = 1210;
	private static final int PriasLetter4 = 1211;
	private static final int MorettisHerb = 1212;
	private static final int MorettisLetter = 1214;
	private static final int PriasLetter = 1215;
	private static final int HonoraryGuard = 1216;
	private static final int ReisasRecommendation = 1217;
	private static final int RustedKey = 1293;
	
	// NPCs
	private static final int Reisa = 30328;
	private static final int Babenco = 30334;
	private static final int Moretti = 30337;
	private static final int Prias = 30426;
	
	public Q407_PathToAnElvenScout(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ReisasLetter,
			PriasLetter1,
			PriasLetter2,
			PriasLetter3,
			PriasLetter4,
			MorettisHerb,
			MorettisLetter,
			PriasLetter,
			HonoraryGuard,
			RustedKey
		};
		
		addStartNpc(Reisa);
		addTalkId(Reisa, Moretti, Babenco, Prias);
		
		addKillId(20053, 27031);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30328-05.htm"))
		{
			if (player.getClassId() != ClassId.elvenFighter)
			{
				if (player.getClassId() == ClassId.elvenScout)
					htmltext = "30328-02a.htm";
				else
					htmltext = "30328-02.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30328-03.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(ReisasRecommendation))
			{
				htmltext = "30328-04.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.giveItems(ReisasLetter, 1);
				st.playSound(QuestState.SOUND_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("30337-03.htm"))
		{
			st.takeItems(ReisasLetter, -1);
			st.set("cond", "2");
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
				htmltext = "30328-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Reisa:
						if (cond == 1)
							htmltext = "30328-06.htm";
						else if (cond >= 2 && cond <= 7)
							htmltext = "30328-08.htm";
						else if (cond == 8)
						{
							htmltext = "30328-07.htm";
							st.takeItems(HonoraryGuard, -1);
							st.giveItems(ReisasRecommendation, 1);
							st.rewardExpAndSp(3200, 1000);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Moretti:
						if (cond == 1)
							htmltext = "30337-01.htm";
						else if (cond == 2)
						{
							if (!st.hasQuestItems(PriasLetter1))
								htmltext = "30337-04.htm";
							else
								htmltext = "30337-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30337-06.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(PriasLetter1, -1);
							st.takeItems(PriasLetter2, -1);
							st.takeItems(PriasLetter3, -1);
							st.takeItems(PriasLetter4, -1);
							st.giveItems(MorettisHerb, 1);
							st.giveItems(MorettisLetter, 1);
						}
						else if (cond >= 4 && cond <= 6)
							htmltext = "30337-09.htm";
						else if (cond == 7 && st.hasQuestItems(PriasLetter))
						{
							htmltext = "30337-07.htm";
							st.set("cond", "8");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(PriasLetter, -1);
							st.giveItems(HonoraryGuard, 1);
						}
						else if (cond == 8)
							htmltext = "30337-08.htm";
						break;
					
					case Babenco:
						if (cond == 2)
							htmltext = "30334-01.htm";
						break;
					
					case Prias:
						if (cond == 4)
						{
							htmltext = "30426-01.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 5)
							htmltext = "30426-01.htm";
						else if (cond == 6)
						{
							htmltext = "30426-02.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(RustedKey, -1);
							st.takeItems(MorettisHerb, -1);
							st.takeItems(MorettisLetter, -1);
							st.giveItems(PriasLetter, 1);
						}
						else if (cond == 7)
							htmltext = "30426-04.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == 20053)
		{
			QuestState st = checkPlayerCondition(player, npc, "cond", "2");
			if (st != null)
			{
				if (!st.hasQuestItems(PriasLetter1))
				{
					st.giveItems(PriasLetter1, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				else if (!st.hasQuestItems(PriasLetter2))
				{
					st.giveItems(PriasLetter2, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				else if (!st.hasQuestItems(PriasLetter3))
				{
					st.giveItems(PriasLetter3, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				else if (!st.hasQuestItems(PriasLetter4))
				{
					st.giveItems(PriasLetter4, 1);
					st.playSound(QuestState.SOUND_MIDDLE);
					st.set("cond", "3");
				}
			}
		}
		else
		{
			QuestState st = checkPlayerCondition(player, npc, "cond", "5");
			if (st != null && st.dropItems(RustedKey, 1, 1, 500000))
				st.set("cond", "6");
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q407_PathToAnElvenScout(407, qn, "Path to an Elven Scout");
	}
}