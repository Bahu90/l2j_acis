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
package quests.Q405_PathToACleric;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q405_PathToACleric extends Quest
{
	private static final String qn = "Q405_PathToACleric";
	
	// Items
	private static final int FirstLetter = 1191;
	private static final int SecondLetter = 1192;
	private static final int LionelsBook = 1193;
	private static final int VivyansBook = 1194;
	private static final int SimplonsBook = 1195;
	private static final int PragasBook = 1196;
	private static final int Certificate = 1197;
	private static final int MothersPendant = 1198;
	private static final int MothersNecklace = 1199;
	private static final int Covenant = 1200;
	
	// NPCs
	private static final int Gallint = 30017;
	private static final int Zigaunt = 30022;
	private static final int Vivyan = 30030;
	private static final int Praga = 30333;
	private static final int Simplon = 30253;
	private static final int Lionel = 30408;
	
	// Reward
	private static final int MarkofFate = 3172;
	
	public Q405_PathToACleric(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			FirstLetter,
			SimplonsBook,
			PragasBook,
			VivyansBook,
			MothersNecklace,
			MothersPendant,
			SecondLetter,
			LionelsBook,
			Certificate,
			Covenant
		};
		
		addStartNpc(Zigaunt);
		addTalkId(Zigaunt, Simplon, Praga, Vivyan, Lionel, Gallint);
		
		addKillId(20029, 20026);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30022-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(FirstLetter, 1);
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
				if (player.getClassId() != ClassId.mage)
				{
					if (player.getClassId() == ClassId.cleric)
						htmltext = "30022-02a.htm";
					else
						htmltext = "30022-02.htm";
					
					st.exitQuest(true);
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30022-03.htm";
					st.exitQuest(true);
				}
				else if (st.hasQuestItems(MarkofFate))
				{
					htmltext = "30022-04.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30022-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Zigaunt:
						if (cond == 1)
							htmltext = "30022-06.htm";
						else if (cond == 2)
						{
							htmltext = "30022-08.htm";
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(PragasBook, 1);
							st.takeItems(VivyansBook, 1);
							st.takeItems(SimplonsBook, 3);
							st.takeItems(FirstLetter, 1);
							st.giveItems(SecondLetter, 1);
						}
						else if (cond >= 3 && cond <= 5)
							htmltext = "30022-07.htm";
						else if (cond == 6)
						{
							htmltext = "30022-09.htm";
							st.takeItems(SecondLetter, 1);
							st.takeItems(Covenant, 1);
							st.giveItems(MarkofFate, 1);
							st.rewardExpAndSp(3200, 5610);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Simplon:
						if (cond == 1 && !st.hasQuestItems(SimplonsBook))
						{
							htmltext = "30253-01.htm";
							st.playSound(QuestState.SOUND_ITEMGET);
							st.giveItems(SimplonsBook, 3);
						}
						else if (cond >= 2 || st.hasQuestItems(SimplonsBook))
							htmltext = "30253-02.htm";
						break;
					
					case Praga:
						if (cond == 1)
						{
							if (!st.hasQuestItems(PragasBook) && !st.hasQuestItems(MothersNecklace) && st.hasQuestItems(SimplonsBook))
							{
								htmltext = "30333-01.htm";
								st.giveItems(MothersNecklace, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
							else if (!st.hasQuestItems(MothersPendant))
								htmltext = "30333-02.htm";
							else if (st.hasQuestItems(MothersPendant))
							{
								htmltext = "30333-03.htm";
								st.takeItems(MothersNecklace, 1);
								st.takeItems(MothersPendant, 1);
								st.giveItems(PragasBook, 1);
								
								if (st.hasQuestItems(VivyansBook))
								{
									st.set("cond", "2");
									st.playSound(QuestState.SOUND_MIDDLE);
								}
								else
									st.playSound(QuestState.SOUND_ITEMGET);
							}
						}
						else if (cond >= 2 || (st.hasQuestItems(PragasBook)))
							htmltext = "30333-04.htm";
						break;
					
					case Vivyan:
						if (cond == 1 && !st.hasQuestItems(VivyansBook) && st.hasQuestItems(SimplonsBook))
						{
							htmltext = "30030-01.htm";
							st.giveItems(VivyansBook, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
							
							if (st.hasQuestItems(PragasBook))
							{
								st.set("cond", "2");
								st.playSound(QuestState.SOUND_MIDDLE);
							}
							else
								st.playSound(QuestState.SOUND_ITEMGET);
						}
						else if (cond >= 2 || st.hasQuestItems(VivyansBook))
							htmltext = "30030-02.htm";
						break;
					
					case Lionel:
						if (cond <= 2)
							htmltext = "30408-02.htm";
						else if (cond == 3)
						{
							htmltext = "30408-01.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(LionelsBook, 1);
						}
						else if (cond == 4)
							htmltext = "30408-03.htm";
						else if (cond == 5)
						{
							htmltext = "30408-04.htm";
							st.set("cond", "6");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(Certificate, 1);
							st.giveItems(Covenant, 1);
						}
						else if (cond == 6)
							htmltext = "30408-05.htm";
						break;
					
					case Gallint:
						if (cond == 4)
						{
							htmltext = "30017-01.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LionelsBook, 1);
							st.giveItems(Certificate, 1);
						}
						else if (cond >= 5)
							htmltext = "30017-02.htm";
						break;
				}
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
		
		if (st.hasQuestItems(MothersNecklace))
			st.dropItems(MothersPendant, 1, 1, 100000);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q405_PathToACleric(405, qn, "Path to a Cleric");
	}
}