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
package quests.Q412_PathToADarkWizard;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q412_PathToADarkWizard extends Quest
{
	private static final String qn = "Q412_PathToADarkWizard";
	
	// Items
	private static final int SeedOfAnger = 1253;
	private static final int SeedOfDespair = 1254;
	private static final int SeedOfHorror = 1255;
	private static final int SeedOfLunacy = 1256;
	private static final int FamilyRemains = 1257;
	private static final int VarikasLiquor = 1258;
	private static final int KneeBone = 1259;
	private static final int HeartOfLunacy = 1260;
	private static final int JewelOfDarkness = 1261;
	private static final int LuckyKey = 1277;
	private static final int Candle = 1278;
	private static final int HubScent = 1279;
	
	// NPCs
	private static final int Varika = 30421;
	private static final int Charkeren = 30415;
	private static final int Annika = 30418;
	private static final int Arkenia = 30419;
	
	public Q412_PathToADarkWizard(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SeedOfAnger,
			SeedOfDespair,
			SeedOfHorror,
			SeedOfLunacy,
			FamilyRemains,
			VarikasLiquor,
			KneeBone,
			HeartOfLunacy,
			LuckyKey,
			Candle,
			HubScent
		};
		
		addStartNpc(Varika);
		addTalkId(Varika, Charkeren, Annika, Arkenia);
		
		addKillId(20015, 20022, 20045, 20517, 20518);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30421-05.htm"))
		{
			if (player.getClassId() != ClassId.darkMage)
			{
				if (player.getClassId() == ClassId.darkWizard)
					htmltext = "30421-02a.htm";
				else
					htmltext = "30421-03.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30421-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(JewelOfDarkness))
			{
				htmltext = "30421-04.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
				st.giveItems(SeedOfDespair, 1);
			}
		}
		else if (event.equalsIgnoreCase("30421-07.htm"))
		{
			if (st.hasQuestItems(SeedOfAnger))
				htmltext = "30421-06.htm";
			else if (st.hasQuestItems(LuckyKey))
				htmltext = "30421-08.htm";
			else if (st.getQuestItemsCount(FamilyRemains) == 3)
				htmltext = "30421-18.htm";
		}
		else if (event.equalsIgnoreCase("30421-10.htm"))
		{
			if (st.hasQuestItems(SeedOfHorror))
				htmltext = "30421-09.htm";
			else if (st.getQuestItemsCount(KneeBone) == 2)
				htmltext = "30421-19.htm";
		}
		else if (event.equalsIgnoreCase("30421-13.htm"))
		{
			if (st.hasQuestItems(SeedOfLunacy))
				htmltext = "30421-12.htm";
		}
		else if (event.equalsIgnoreCase("30415-03.htm"))
		{
			st.giveItems(LuckyKey, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30418-02.htm"))
		{
			st.giveItems(Candle, 1);
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
				htmltext = "30421-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case Varika:
						if (st.hasQuestItems(SeedOfAnger) && st.hasQuestItems(SeedOfHorror) && st.hasQuestItems(SeedOfLunacy))
						{
							htmltext = "30421-16.htm";
							st.takeItems(SeedOfAnger, 1);
							st.takeItems(SeedOfDespair, 1);
							st.takeItems(SeedOfHorror, 1);
							st.takeItems(SeedOfLunacy, 1);
							st.giveItems(JewelOfDarkness, 1);
							st.rewardExpAndSp(3200, 1650);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						else
							htmltext = "30421-17.htm";
						break;
					
					case Charkeren:
						if (st.hasQuestItems(SeedOfAnger))
							htmltext = "30415-06.htm";
						else if (!st.hasQuestItems(LuckyKey))
							htmltext = "30415-01.htm";
						else if (st.getQuestItemsCount(FamilyRemains) == 3)
						{
							htmltext = "30415-05.htm";
							st.takeItems(FamilyRemains, -1);
							st.takeItems(LuckyKey, 1);
							st.giveItems(SeedOfAnger, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else
							htmltext = "30415-04.htm";
						break;
					
					case Annika:
						if (st.hasQuestItems(SeedOfHorror))
							htmltext = "30418-04.htm";
						else if (!st.hasQuestItems(Candle))
							htmltext = "30418-01.htm";
						else if (st.getQuestItemsCount(KneeBone) == 2)
						{
							htmltext = "30418-04.htm";
							st.takeItems(Candle, 1);
							st.takeItems(KneeBone, -1);
							st.giveItems(SeedOfHorror, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else
							htmltext = "30418-03.htm";
						break;
					
					case Arkenia:
						if (st.hasQuestItems(SeedOfLunacy))
							htmltext = "30419-03.htm";
						else if (!st.hasQuestItems(HubScent))
						{
							htmltext = "30419-01.htm";
							st.giveItems(HubScent, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (st.getQuestItemsCount(HeartOfLunacy) == 3)
						{
							htmltext = "30419-03.htm";
							st.takeItems(HeartOfLunacy, -1);
							st.takeItems(HubScent, 1);
							st.giveItems(SeedOfLunacy, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else
							htmltext = "30419-02.htm";
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
			case 20015:
				if (st.hasQuestItems(LuckyKey))
					st.dropItems(FamilyRemains, 1, 3, 333333);
				break;
			
			case 20022:
			case 20517:
			case 20518:
				if (st.hasQuestItems(Candle))
					st.dropItems(KneeBone, 1, 2, 333333);
				break;
			
			case 20045:
				if (st.hasQuestItems(HubScent))
					st.dropItems(HeartOfLunacy, 1, 3, 333333);
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q412_PathToADarkWizard(412, qn, "Path to a Dark Wizard");
	}
}