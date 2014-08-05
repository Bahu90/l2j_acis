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
package quests.Q413_PathToAShillienOracle;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q413_PathToAShillienOracle extends Quest
{
	private static final String qn = "Q413_PathToAShillienOracle";
	
	// Items
	private static final int SidrasLetter = 1262;
	private static final int BlankSheet = 1263;
	private static final int BloodyRune = 1264;
	private static final int GarmielBook = 1265;
	private static final int PrayerofAdonius = 1266;
	private static final int PenitentsMark = 1267;
	private static final int AshenBones = 1268;
	private static final int AndarielBook = 1269;
	private static final int OrbofAbyss = 1270;
	
	// NPCs
	private static final int Sidra = 30330;
	private static final int Adonius = 30375;
	private static final int Talbot = 30377;
	
	public Q413_PathToAShillienOracle(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SidrasLetter,
			BlankSheet,
			BloodyRune,
			GarmielBook,
			PrayerofAdonius,
			PenitentsMark,
			AshenBones,
			AndarielBook
		};
		
		addStartNpc(Sidra);
		addTalkId(Sidra, Adonius, Talbot);
		
		addKillId(20776, 20457, 20458, 20514, 20515);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30330-05.htm"))
		{
			if (player.getClassId() != ClassId.darkMage)
			{
				if (player.getClassId() == ClassId.shillienOracle)
					htmltext = "30330-02a.htm";
				else
					htmltext = "30330-03.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30330-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(OrbofAbyss))
			{
				htmltext = "30330-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30330-06.htm"))
		{
			st.set("cond", "1");
			st.giveItems(SidrasLetter, 1);
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30377-02.htm"))
		{
			st.set("cond", "2");
			st.takeItems(SidrasLetter, 1);
			st.giveItems(BlankSheet, 5);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30375-04.htm"))
		{
			st.set("cond", "5");
			st.takeItems(PrayerofAdonius, 1);
			st.giveItems(PenitentsMark, 1);
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
				htmltext = "30330-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Sidra:
						if (cond == 1)
							htmltext = "30330-07.htm";
						else if (cond >= 2 && cond <= 3)
							htmltext = "30330-08.htm";
						else if (cond >= 4 && cond <= 6)
							htmltext = "30330-09.htm";
						else if (cond == 7)
						{
							htmltext = "30330-10.htm";
							st.takeItems(GarmielBook, 1);
							st.takeItems(AndarielBook, 1);
							st.giveItems(OrbofAbyss, 1);
							st.rewardExpAndSp(3200, 3120);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Talbot:
						if (cond == 1)
							htmltext = "30377-01.htm";
						else if (cond == 2)
						{
							if (st.hasQuestItems(BloodyRune))
								htmltext = "30377-04.htm";
							else
								htmltext = "30377-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30377-05.htm";
							st.set("cond", "4");
							st.takeItems(BloodyRune, -1);
							st.giveItems(GarmielBook, 1);
							st.giveItems(PrayerofAdonius, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond >= 4 && cond <= 6)
							htmltext = "30377-06.htm";
						else if (cond == 7)
							htmltext = "30377-07.htm";
						break;
					case Adonius:
						if (cond == 4)
							htmltext = "30375-01.htm";
						else if (cond == 5)
						{
							if (st.hasQuestItems(AshenBones))
								htmltext = "30375-05.htm";
							else
								htmltext = "30375-06.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30375-07.htm";
							st.set("cond", "7");
							st.takeItems(AshenBones, -1);
							st.takeItems(PenitentsMark, -1);
							st.giveItems(AndarielBook, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 7)
							htmltext = "30375-08.htm";
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
		
		if (npc.getNpcId() == 20776)
		{
			if (st.getInt("cond") == 2)
			{
				st.takeItems(BlankSheet, 1);
				if (st.dropItemsAlways(BloodyRune, 1, 5))
					st.set("cond", "3");
			}
		}
		else
		{
			if (st.getInt("cond") == 5)
				if (st.dropItemsAlways(AshenBones, 1, 10))
					st.set("cond", "6");
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q413_PathToAShillienOracle(413, qn, "Path to a Shillien Oracle");
	}
}