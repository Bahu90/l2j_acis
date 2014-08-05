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
package quests.Q401_PathToAWarrior;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q401_PathToAWarrior extends Quest
{
	private static final String qn = "Q401_PathToAWarrior";
	
	// Items
	private static final int AuronsLetter = 1138;
	private static final int WarriorGuildMark = 1139;
	private static final int RustedBronzeSword1 = 1140;
	private static final int RustedBronzeSword2 = 1141;
	private static final int RustedBronzeSword3 = 1142;
	private static final int SimplonsLetter = 1143;
	private static final int PoisonSpiderLeg = 1144;
	private static final int MedallionOfWarrior = 1145;
	
	// NPCs
	private static final int Auron = 30010;
	private static final int Simplon = 30253;
	
	public Q401_PathToAWarrior(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			AuronsLetter,
			WarriorGuildMark,
			RustedBronzeSword1,
			RustedBronzeSword2,
			RustedBronzeSword3,
			SimplonsLetter,
			PoisonSpiderLeg
		};
		
		addStartNpc(Auron);
		addTalkId(Auron, Simplon);
		
		addKillId(20035, 20038, 20042, 20043);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30010-05.htm"))
		{
			if (player.getClassId() != ClassId.fighter)
			{
				if (player.getClassId() == ClassId.warrior)
					htmltext = "30010-03.htm";
				else
					htmltext = "30010-02b.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30010-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(MedallionOfWarrior))
			{
				htmltext = "30010-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30010-06.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.giveItems(AuronsLetter, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30253-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(AuronsLetter, 1);
			st.giveItems(WarriorGuildMark, 1);
		}
		else if (event.equalsIgnoreCase("30010-11.htm"))
		{
			st.set("cond", "5");
			st.takeItems(RustedBronzeSword2, 1);
			st.giveItems(RustedBronzeSword3, 1);
			st.takeItems(SimplonsLetter, 1);
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
				htmltext = "30010-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Auron:
						if (cond == 1)
							htmltext = "30010-07.htm";
						else if (cond == 2 || cond == 3)
							htmltext = "30010-08.htm";
						else if (cond == 4)
							htmltext = "30010-09.htm";
						else if (cond == 5)
							htmltext = "30010-12.htm";
						else if (cond == 6)
						{
							htmltext = "30010-13.htm";
							st.takeItems(RustedBronzeSword3, 1);
							st.takeItems(PoisonSpiderLeg, -1);
							st.giveItems(MedallionOfWarrior, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Simplon:
						if (cond == 1)
							htmltext = "30253-01.htm";
						else if (cond == 2)
						{
							if (st.getQuestItemsCount(RustedBronzeSword1) == 0)
								htmltext = "30253-03.htm";
							else if (st.getQuestItemsCount(RustedBronzeSword1) <= 9)
								htmltext = "30253-03b.htm";
						}
						else if (cond == 3)
						{
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(WarriorGuildMark, 1);
							st.takeItems(RustedBronzeSword1, 10);
							st.giveItems(RustedBronzeSword2, 1);
							st.giveItems(SimplonsLetter, 1);
							htmltext = "30253-04.htm";
						}
						else if (cond == 4)
							htmltext = "30253-05.htm";
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
		
		switch (npc.getNpcId())
		{
			case 20035:
			case 20042:
				if (st.getInt("cond") == 2)
					if (st.dropItems(RustedBronzeSword1, 1, 10, 400000))
						st.set("cond", "3");
				break;
			
			case 20038:
			case 20043:
				if (st.getInt("cond") == 5 && (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == RustedBronzeSword3))
					if (st.dropItemsAlways(PoisonSpiderLeg, 1, 20))
						st.set("cond", "6");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q401_PathToAWarrior(401, qn, "Path to a Warrior");
	}
}