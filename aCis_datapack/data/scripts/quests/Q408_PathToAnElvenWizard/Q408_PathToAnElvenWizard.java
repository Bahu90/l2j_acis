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
package quests.Q408_PathToAnElvenWizard;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q408_PathToAnElvenWizard extends Quest
{
	private static final String qn = "Q408_PathToAnElvenWizard";
	
	// Items
	private static final int RosellasLetter = 1218;
	private static final int RedDown = 1219;
	private static final int MagicalPowersRuby = 1220;
	private static final int PureAquamarine = 1221;
	private static final int AppetizingApple = 1222;
	private static final int GoldenLeaves = 1223;
	private static final int ImmortalLove = 1224;
	private static final int Amethyst = 1225;
	private static final int NobilityAmethyst = 1226;
	private static final int FertilityPeridot = 1229;
	private static final int EternityDiamond = 1230;
	private static final int CharmOfGrain = 1272;
	private static final int SapOfTheMotherTree = 1273;
	private static final int LuckyPotpourri = 1274;
	
	// NPCs
	private static final int Rosella = 30414;
	private static final int Greenis = 30157;
	private static final int Thalia = 30371;
	private static final int Northwind = 30423;
	
	public Q408_PathToAnElvenWizard(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			RosellasLetter,
			RedDown,
			MagicalPowersRuby,
			PureAquamarine,
			AppetizingApple,
			GoldenLeaves,
			ImmortalLove,
			Amethyst,
			NobilityAmethyst,
			FertilityPeridot,
			CharmOfGrain,
			SapOfTheMotherTree,
			LuckyPotpourri
		};
		
		addStartNpc(Rosella);
		addTalkId(Rosella, Greenis, Thalia, Northwind);
		
		addKillId(20047, 20019, 20466);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30414-06.htm"))
		{
			if (player.getClassId() != ClassId.elvenMage)
			{
				if (player.getClassId() == ClassId.elvenWizard)
					htmltext = "30414-02a.htm";
				else
					htmltext = "30414-03.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30414-04.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(EternityDiamond))
			{
				htmltext = "30414-05.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
				st.giveItems(FertilityPeridot, 1);
			}
		}
		else if (event.equalsIgnoreCase("30414-07.htm"))
		{
			if (!st.hasQuestItems(MagicalPowersRuby))
			{
				st.giveItems(RosellasLetter, 1);
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else
				htmltext = "30414-10.htm";
		}
		else if (event.equalsIgnoreCase("30414-14.htm"))
		{
			if (!st.hasQuestItems(PureAquamarine))
			{
				st.giveItems(AppetizingApple, 1);
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else
				htmltext = "30414-13.htm";
		}
		else if (event.equalsIgnoreCase("30414-18.htm"))
		{
			if (!st.hasQuestItems(NobilityAmethyst))
			{
				st.giveItems(ImmortalLove, 1);
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else
				htmltext = "30414-17.htm";
		}
		else if (event.equalsIgnoreCase("30157-02.htm"))
		{
			st.takeItems(RosellasLetter, 1);
			st.giveItems(CharmOfGrain, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30371-02.htm"))
		{
			st.takeItems(AppetizingApple, 1);
			st.giveItems(SapOfTheMotherTree, 1);
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
				htmltext = "30414-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case Rosella:
						if (st.hasQuestItems(MagicalPowersRuby) && st.hasQuestItems(PureAquamarine) && st.hasQuestItems(NobilityAmethyst))
						{
							htmltext = "30414-24.htm";
							st.takeItems(MagicalPowersRuby, 1);
							st.takeItems(PureAquamarine, 1);
							st.takeItems(NobilityAmethyst, 1);
							st.takeItems(FertilityPeridot, 1);
							st.giveItems(EternityDiamond, 1);
							st.rewardExpAndSp(3200, 1890);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						else if (st.hasQuestItems(RosellasLetter))
							htmltext = "30414-08.htm";
						else if (st.hasQuestItems(CharmOfGrain))
						{
							if (st.getQuestItemsCount(RedDown) == 5)
								htmltext = "30414-25.htm";
							else
								htmltext = "30414-09.htm";
						}
						else if (st.hasQuestItems(AppetizingApple))
							htmltext = "30414-15.htm";
						else if (st.hasQuestItems(SapOfTheMotherTree))
						{
							if (st.getQuestItemsCount(GoldenLeaves) == 5)
								htmltext = "30414-26.htm";
							else
								htmltext = "30414-16.htm";
						}
						else if (st.hasQuestItems(ImmortalLove))
							htmltext = "30414-19.htm";
						else if (st.hasQuestItems(LuckyPotpourri))
						{
							if (st.getQuestItemsCount(Amethyst) == 2)
								htmltext = "30414-27.htm";
							else
								htmltext = "30414-20.htm";
						}
						else
							htmltext = "30414-11.htm";
						break;
					
					case Greenis:
						if (st.hasQuestItems(RosellasLetter))
							htmltext = "30157-01.htm";
						else if (st.getQuestItemsCount(RedDown) == 5)
						{
							htmltext = "30157-04.htm";
							st.takeItems(RedDown, -1);
							st.takeItems(CharmOfGrain, 1);
							st.giveItems(MagicalPowersRuby, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (st.hasQuestItems(CharmOfGrain))
							htmltext = "30157-03.htm";
						break;
					
					case Thalia:
						if (st.hasQuestItems(AppetizingApple))
							htmltext = "30371-01.htm";
						else if (st.getQuestItemsCount(GoldenLeaves) == 5)
						{
							htmltext = "30371-04.htm";
							st.takeItems(GoldenLeaves, -1);
							st.takeItems(SapOfTheMotherTree, 1);
							st.giveItems(PureAquamarine, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (st.hasQuestItems(SapOfTheMotherTree))
							htmltext = "30371-03.htm";
						break;
					
					case Northwind:
						if (st.hasQuestItems(ImmortalLove))
						{
							htmltext = "30423-01.htm";
							st.takeItems(ImmortalLove, 1);
							st.giveItems(LuckyPotpourri, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (st.getQuestItemsCount(Amethyst) == 2)
						{
							htmltext = "30423-03.htm";
							st.takeItems(LuckyPotpourri, 1);
							st.takeItems(Amethyst, -1);
							st.giveItems(NobilityAmethyst, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (st.hasQuestItems(LuckyPotpourri))
							htmltext = "30423-02.htm";
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
			case 20019:
				if (st.hasQuestItems(SapOfTheMotherTree))
					st.dropItems(GoldenLeaves, 1, 5, 400000);
				break;
			
			case 20047:
				if (st.hasQuestItems(LuckyPotpourri))
					st.dropItems(Amethyst, 1, 2, 400000);
				break;
			
			case 20466:
				if (st.hasQuestItems(CharmOfGrain))
					st.dropItems(RedDown, 1, 5, 700000);
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q408_PathToAnElvenWizard(408, qn, "Path to an Elven Wizard");
	}
}