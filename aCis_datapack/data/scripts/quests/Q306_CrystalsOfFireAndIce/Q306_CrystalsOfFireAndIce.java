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
package quests.Q306_CrystalsOfFireAndIce;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q306_CrystalsOfFireAndIce extends Quest
{
	private static final String qn = "Q306_CrystalsOfFireAndIce";
	
	// Items
	private static final int FLAME_SHARD = 1020;
	private static final int ICE_SHARD = 1021;
	
	// NPC
	private static final int KATERINA = 30004;
	
	// Droplist
	Map<Integer, int[]> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(20109, new int[]
		{
			300000,
			FLAME_SHARD
		});
		DROPLIST.put(20110, new int[]
		{
			300000,
			ICE_SHARD
		});
		DROPLIST.put(20112, new int[]
		{
			400000,
			FLAME_SHARD
		});
		DROPLIST.put(20113, new int[]
		{
			400000,
			ICE_SHARD
		});
		DROPLIST.put(20114, new int[]
		{
			500000,
			FLAME_SHARD
		});
		DROPLIST.put(20115, new int[]
		{
			500000,
			ICE_SHARD
		});
	}
	
	public Q306_CrystalsOfFireAndIce(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			FLAME_SHARD,
			ICE_SHARD
		};
		
		addStartNpc(KATERINA);
		addTalkId(KATERINA);
		
		for (int mob : DROPLIST.keySet())
			addKillId(mob);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30004-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30004-08.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
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
				if (player.getLevel() >= 17)
					htmltext = "30004-03.htm";
				else
				{
					htmltext = "30004-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int flame = st.getQuestItemsCount(FLAME_SHARD);
				int ice = st.getQuestItemsCount(ICE_SHARD);
				
				if (flame + ice == 0)
					htmltext = "30004-05.htm";
				else
				{
					htmltext = "30004-07.htm";
					st.playSound(QuestState.SOUND_MIDDLE);
					
					if (flame + ice > 10)
						st.rewardItems(57, 5000 + (30 * (flame + ice)));
					else
						st.rewardItems(57, 30 * (flame + ice));
					
					st.takeItems(FLAME_SHARD, -1);
					st.takeItems(ICE_SHARD, -1);
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
		
		int npcId = npc.getNpcId();
		if (DROPLIST.containsKey(npcId))
			st.dropItems(DROPLIST.get(npcId)[1], 1, 0, DROPLIST.get(npcId)[0]);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q306_CrystalsOfFireAndIce(306, qn, "Crystals of Fire and Ice");
	}
}