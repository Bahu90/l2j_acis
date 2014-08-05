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
package quests.Q271_ProofOfValor;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q271_ProofOfValor extends Quest
{
	private static final String qn = "Q271_ProofOfValor";
	
	// Items
	private static final int KASHA_WOLF_FANG = 1473;
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	
	// NPC
	private static final int RUKAIN = 30577;
	
	// Mob
	private static final int KASHA_WOLF = 20475;
	
	public Q271_ProofOfValor(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			KASHA_WOLF_FANG
		};
		
		addStartNpc(RUKAIN);
		addTalkId(RUKAIN);
		
		addKillId(KASHA_WOLF);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30577-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			
			if (st.hasQuestItems(NECKLACE_OF_COURAGE) || st.hasQuestItems(NECKLACE_OF_VALOR))
				htmltext = "30577-07.htm";
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
				if (player.getRace() != Race.Orc)
					htmltext = "30577-00.htm";
				else if (player.getLevel() < 4)
					htmltext = "30577-01.htm";
				else
				{
					// Different HTM if you are repeating the quest.
					if (st.hasQuestItems(NECKLACE_OF_COURAGE) || st.hasQuestItems(NECKLACE_OF_VALOR))
						htmltext = "30577-06.htm";
					else
						htmltext = "30577-02.htm";
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
				{
					if (st.hasQuestItems(NECKLACE_OF_COURAGE) || st.hasQuestItems(NECKLACE_OF_VALOR))
						htmltext = "30577-07.htm";
					else
						htmltext = "30577-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "30577-05.htm";
					st.takeItems(KASHA_WOLF_FANG, -1);
					
					if (Rnd.get(100) <= 10)
						st.giveItems(NECKLACE_OF_VALOR, 1);
					else
						st.giveItems(NECKLACE_OF_COURAGE, 1);
					
					st.unset("cond"); // Reset cond
					
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
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
		
		if (st.dropItems(KASHA_WOLF_FANG, 1, 50, 250000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q271_ProofOfValor(271, qn, "Proof of Valor");
	}
}