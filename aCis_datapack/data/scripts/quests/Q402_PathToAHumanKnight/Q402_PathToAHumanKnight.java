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
package quests.Q402_PathToAHumanKnight;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q402_PathToAHumanKnight extends Quest
{
	private static final String qn = "Q402_PathToAHumanKnight";
	
	// Items
	private static final int SwordOfRitual = 1161;
	private static final int CoinOfLords1 = 1162;
	private static final int CoinOfLords2 = 1163;
	private static final int CoinOfLords3 = 1164;
	private static final int CoinOfLords4 = 1165;
	private static final int CoinOfLords5 = 1166;
	private static final int CoinOfLords6 = 1167;
	private static final int GludioGuardsMark1 = 1168;
	private static final int BugbearNecklace = 1169;
	private static final int EinhasadChurchMark1 = 1170;
	private static final int EinhasadCrucifix = 1171;
	private static final int GludioGuardsMark2 = 1172;
	private static final int SpiderLeg = 1173;
	private static final int EinhasadChurchMark2 = 1174;
	private static final int LizardmanTotem = 1175;
	private static final int GludioGuardsMark3 = 1176;
	private static final int GiantSpiderHusk = 1177;
	private static final int EinhasadChurchMark3 = 1178;
	private static final int HorribleSkull = 1179;
	private static final int MarkOfEsquire = 1271;
	
	// NPCs
	private static final int SirKlausVasper = 30417;
	private static final int Bathis = 30332;
	private static final int Raymond = 30289;
	private static final int Bezique = 30379;
	private static final int Levian = 30037;
	private static final int Gilbert = 30039;
	private static final int Biotin = 30031;
	private static final int SirAaronTanford = 30653;
	private static final int SirCollinWindawood = 30311;
	
	public Q402_PathToAHumanKnight(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MarkOfEsquire,
			CoinOfLords1,
			CoinOfLords2,
			CoinOfLords3,
			CoinOfLords4,
			CoinOfLords5,
			CoinOfLords6,
			GludioGuardsMark1,
			BugbearNecklace,
			EinhasadChurchMark1,
			EinhasadCrucifix,
			GludioGuardsMark2,
			SpiderLeg,
			EinhasadChurchMark2,
			LizardmanTotem,
			GludioGuardsMark3,
			GiantSpiderHusk,
			EinhasadChurchMark3,
			LizardmanTotem,
			GludioGuardsMark3,
			GiantSpiderHusk,
			EinhasadChurchMark3,
			HorribleSkull
		};
		
		addStartNpc(SirKlausVasper);
		addTalkId(SirKlausVasper, Bathis, Raymond, Bezique, Levian, Gilbert, Biotin, SirAaronTanford, SirCollinWindawood);
		
		addKillId(20775, 27024, 20038, 20043, 20050, 20030, 20027, 20024, 20103, 20106, 20108, 20404);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30417-05.htm"))
		{
			if (player.getClassId() != ClassId.fighter)
			{
				if (player.getClassId() == ClassId.knight)
					htmltext = "30417-02a.htm";
				else
					htmltext = "30417-03.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30417-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(SwordOfRitual))
			{
				htmltext = "30417-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30417-08.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(MarkOfEsquire, 1);
		}
		else if (event.equalsIgnoreCase("30332-02.htm"))
		{
			st.giveItems(GludioGuardsMark1, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30289-03.htm"))
		{
			st.giveItems(EinhasadChurchMark1, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30379-02.htm"))
		{
			st.giveItems(GludioGuardsMark2, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30037-02.htm"))
		{
			st.giveItems(EinhasadChurchMark2, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30039-02.htm"))
		{
			st.giveItems(GludioGuardsMark3, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30031-02.htm"))
		{
			st.giveItems(EinhasadChurchMark3, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30417-13.htm") || event.equalsIgnoreCase("30417-14.htm"))
		{
			int coinCount = st.getQuestItemsCount(CoinOfLords1) + st.getQuestItemsCount(CoinOfLords2) + st.getQuestItemsCount(CoinOfLords3) + st.getQuestItemsCount(CoinOfLords4) + st.getQuestItemsCount(CoinOfLords5) + st.getQuestItemsCount(CoinOfLords6);
			
			st.takeItems(CoinOfLords1, -1);
			st.takeItems(CoinOfLords2, -1);
			st.takeItems(CoinOfLords3, -1);
			st.takeItems(CoinOfLords4, -1);
			st.takeItems(CoinOfLords5, -1);
			st.takeItems(CoinOfLords6, -1);
			st.takeItems(MarkOfEsquire, 1);
			st.giveItems(SwordOfRitual, 1);
			st.rewardExpAndSp(3200, 1500 + (1920 * (coinCount - 3)));
			player.broadcastPacket(new SocialAction(player, 3));
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
				htmltext = "30417-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case SirKlausVasper:
						int coins = st.getQuestItemsCount(CoinOfLords1) + st.getQuestItemsCount(CoinOfLords2) + st.getQuestItemsCount(CoinOfLords3) + st.getQuestItemsCount(CoinOfLords4) + st.getQuestItemsCount(CoinOfLords5) + st.getQuestItemsCount(CoinOfLords6);
						if (coins < 3)
							htmltext = "30417-09.htm";
						else if (coins == 3)
							htmltext = "30417-10.htm";
						else if (coins >= 4 && coins <= 5)
							htmltext = "30417-11.htm";
						else if (coins == 6)
						{
							htmltext = "30417-12.htm";
							st.takeItems(CoinOfLords1, -1);
							st.takeItems(CoinOfLords2, -1);
							st.takeItems(CoinOfLords3, -1);
							st.takeItems(CoinOfLords4, -1);
							st.takeItems(CoinOfLords5, -1);
							st.takeItems(CoinOfLords6, -1);
							st.takeItems(MarkOfEsquire, 1);
							st.giveItems(SwordOfRitual, 1);
							st.rewardExpAndSp(3200, 7260);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Bathis:
						if (!st.hasQuestItems(GludioGuardsMark1) && !st.hasQuestItems(CoinOfLords1))
							htmltext = "30332-01.htm";
						else if (st.hasQuestItems(GludioGuardsMark1))
						{
							if (st.getQuestItemsCount(BugbearNecklace) < 10)
								htmltext = "30332-03.htm";
							else
							{
								htmltext = "30332-04.htm";
								st.takeItems(BugbearNecklace, -1);
								st.takeItems(GludioGuardsMark1, 1);
								st.giveItems(CoinOfLords1, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords1))
							htmltext = "30332-05.htm";
						break;
					
					case Raymond:
						if (!st.hasQuestItems(EinhasadChurchMark1) && !st.hasQuestItems(CoinOfLords2))
							htmltext = "30289-01.htm";
						else if (st.hasQuestItems(EinhasadChurchMark1))
						{
							if (st.getQuestItemsCount(EinhasadCrucifix) < 12)
								htmltext = "30289-04.htm";
							else
							{
								htmltext = "30289-05.htm";
								st.takeItems(EinhasadCrucifix, -1);
								st.takeItems(EinhasadChurchMark1, 1);
								st.giveItems(CoinOfLords2, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords2))
							htmltext = "30289-06.htm";
						break;
					
					case Bezique:
						if (!st.hasQuestItems(GludioGuardsMark2) && !st.hasQuestItems(CoinOfLords3))
							htmltext = "30379-01.htm";
						else if (st.hasQuestItems(GludioGuardsMark2))
						{
							if (st.getQuestItemsCount(SpiderLeg) < 20)
								htmltext = "30379-03.htm";
							else
							{
								htmltext = "30379-04.htm";
								st.takeItems(SpiderLeg, -1);
								st.takeItems(GludioGuardsMark2, 1);
								st.giveItems(CoinOfLords3, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords3))
							htmltext = "30379-05.htm";
						break;
					
					case Levian:
						if (!st.hasQuestItems(EinhasadChurchMark2) && !st.hasQuestItems(CoinOfLords4))
							htmltext = "30037-01.htm";
						else if (st.hasQuestItems(EinhasadChurchMark2))
						{
							if (st.getQuestItemsCount(LizardmanTotem) < 20)
								htmltext = "30037-03.htm";
							else
							{
								htmltext = "30037-04.htm";
								st.takeItems(LizardmanTotem, -1);
								st.takeItems(EinhasadChurchMark2, 1);
								st.giveItems(CoinOfLords4, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords4))
							htmltext = "30037-05.htm";
						break;
					
					case Gilbert:
						if (!st.hasQuestItems(GludioGuardsMark3) && !st.hasQuestItems(CoinOfLords5))
							htmltext = "30039-01.htm";
						else if (st.hasQuestItems(GludioGuardsMark3))
						{
							if (st.getQuestItemsCount(GiantSpiderHusk) < 20)
								htmltext = "30039-03.htm";
							else
							{
								htmltext = "30039-04.htm";
								st.takeItems(GiantSpiderHusk, -1);
								st.takeItems(GludioGuardsMark3, 1);
								st.giveItems(CoinOfLords5, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords5))
							htmltext = "30039-05.htm";
						break;
					
					case Biotin:
						if (!st.hasQuestItems(EinhasadChurchMark3) && !st.hasQuestItems(CoinOfLords6))
							htmltext = "30031-01.htm";
						else if (st.hasQuestItems(EinhasadChurchMark3))
						{
							if (st.getQuestItemsCount(HorribleSkull) < 10)
								htmltext = "30031-03.htm";
							else
							{
								htmltext = "30031-04.htm";
								st.takeItems(HorribleSkull, -1);
								st.takeItems(EinhasadChurchMark3, 1);
								st.giveItems(CoinOfLords6, 1);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (st.hasQuestItems(CoinOfLords6))
							htmltext = "30031-05.htm";
						break;
					
					case SirAaronTanford:
						htmltext = "30653-01.htm";
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
			case 20775: // Bugbear Raider
				if (st.hasQuestItems(GludioGuardsMark1))
					st.dropItemsAlways(BugbearNecklace, 1, 10);
				break;
			
			case 27024: // Undead Priest
				if (st.hasQuestItems(EinhasadChurchMark1))
					st.dropItems(EinhasadCrucifix, 1, 12, 400000);
				break;
			
			case 20038: // Poison Spider
			case 20043: // Arachnid Tracker
			case 20050: // Arachnid Predator
				if (st.hasQuestItems(GludioGuardsMark2))
					st.dropItems(SpiderLeg, 1, 20, 400000);
				break;
			
			case 20030: // Langk Lizardman
			case 20027: // Langk Lizardman Scout
			case 20024: // Langk Lizardman Warrior
				if (st.hasQuestItems(EinhasadChurchMark2))
					st.dropItems(LizardmanTotem, 1, 20, 400000);
				break;
			
			case 20103: // Giant Spider
			case 20106: // Talon Spider
			case 20108: // Blade Spider
				if (st.hasQuestItems(GludioGuardsMark3))
					st.dropItems(GiantSpiderHusk, 1, 20, 400000);
				break;
			
			case 20404: // Silent Horror
				if (st.hasQuestItems(EinhasadChurchMark3))
					st.dropItems(HorribleSkull, 1, 10, 400000);
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q402_PathToAHumanKnight(402, qn, "Path to a Human Knight");
	}
}