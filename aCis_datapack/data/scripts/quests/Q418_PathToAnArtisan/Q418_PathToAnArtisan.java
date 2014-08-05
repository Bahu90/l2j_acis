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
package quests.Q418_PathToAnArtisan;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q418_PathToAnArtisan extends Quest
{
	private static final String qn = "Q418_PathToAnArtisan";
	
	// Items
	private static final int SilverasRing = 1632;
	private static final int FirstPassCertificate = 1633;
	private static final int SecondPassCertificate = 1634;
	private static final int FinalPassCertificate = 1635;
	private static final int BoogleRatmanTooth = 1636;
	private static final int BoogleRatmanLeadersTooth = 1637;
	private static final int KlutosLetter = 1638;
	private static final int FootprintOfThief = 1639;
	private static final int StolenSecretBox = 1640;
	private static final int SecretBox = 1641;
	
	// NPCs
	private static final int Silvera = 30527;
	private static final int Kluto = 30317;
	private static final int Pinter = 30298;
	private static final int Obi = 32052;
	private static final int Hitchi = 31963;
	private static final int Lockirin = 30531;
	private static final int Rydel = 31956;
	
	public Q418_PathToAnArtisan(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SilverasRing,
			FirstPassCertificate,
			SecondPassCertificate,
			BoogleRatmanTooth,
			BoogleRatmanLeadersTooth,
			KlutosLetter,
			FootprintOfThief,
			StolenSecretBox,
			SecretBox
		};
		
		addStartNpc(Silvera);
		addTalkId(Silvera, Kluto, Pinter, Obi, Hitchi, Lockirin, Rydel);
		
		addKillId(20389, 20390, 20017);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30527-05.htm"))
		{
			if (player.getClassId() != ClassId.dwarvenFighter)
			{
				if (player.getClassId() == ClassId.artisan)
					htmltext = "30527-02a.htm";
				else
					htmltext = "30527-02.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30527-03.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(FinalPassCertificate))
			{
				htmltext = "30527-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30527-06.htm"))
		{
			st.set("cond", "1");
			st.giveItems(SilverasRing, 1);
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30527-08a.htm"))
		{
			st.set("cond", "3");
			st.takeItems(BoogleRatmanLeadersTooth, -1);
			st.takeItems(BoogleRatmanTooth, -1);
			st.takeItems(SilverasRing, 1);
			st.giveItems(FirstPassCertificate, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30527-08b.htm"))
		{
			st.set("cond", "8");
			st.takeItems(BoogleRatmanLeadersTooth, -1);
			st.takeItems(BoogleRatmanTooth, -1);
			st.takeItems(SilverasRing, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30317-04.htm") || event.equalsIgnoreCase("30317-07.htm"))
		{
			st.set("cond", "4");
			st.giveItems(KlutosLetter, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30317-10.htm"))
		{
			st.takeItems(FirstPassCertificate, 1);
			st.takeItems(SecondPassCertificate, 1);
			st.takeItems(SecretBox, 1);
			st.giveItems(FinalPassCertificate, 1);
			st.rewardExpAndSp(3200, 6980);
			player.broadcastPacket(new SocialAction(player, 3));
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30317-12.htm") || event.equalsIgnoreCase("30531-05.htm") || event.equalsIgnoreCase("32052-11.htm") || event.equalsIgnoreCase("31963-10.htm") || event.equalsIgnoreCase("31956-04.htm"))
		{
			st.takeItems(FirstPassCertificate, 1);
			st.takeItems(SecondPassCertificate, 1);
			st.takeItems(SecretBox, 1);
			st.giveItems(FinalPassCertificate, 1);
			st.rewardExpAndSp(3200, 3490);
			player.broadcastPacket(new SocialAction(player, 3));
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30298-03.htm"))
		{
			st.set("cond", "5");
			st.takeItems(KlutosLetter, -1);
			st.giveItems(FootprintOfThief, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30298-06.htm"))
		{
			st.set("cond", "7");
			st.takeItems(StolenSecretBox, -1);
			st.takeItems(FootprintOfThief, -1);
			st.giveItems(SecretBox, 1);
			st.giveItems(SecondPassCertificate, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32052-06.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31963-04.htm"))
		{
			st.set("cond", "10");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31963-05.htm"))
		{
			st.set("cond", "11");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31963-07.htm"))
		{
			st.set("cond", "12");
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
				htmltext = "30527-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Silvera:
						if (cond == 1)
							htmltext = "30527-07.htm";
						else if (cond == 2)
							htmltext = "30527-08.htm";
						else if (cond == 3)
							htmltext = "30527-09.htm";
						else if (cond == 8)
							htmltext = "30527-09a.htm";
						break;
					
					case Kluto:
						if (cond == 3)
							htmltext = "30317-01.htm";
						else if (cond == 4)
							htmltext = "30317-08.htm";
						else if (cond == 7)
							htmltext = "30317-09.htm";
						break;
					
					case Pinter:
						if (cond == 4)
							htmltext = "30298-01.htm";
						else if (cond == 5)
							htmltext = "30298-04.htm";
						else if (cond == 6)
							htmltext = "30298-05.htm";
						else if (cond == 7)
							htmltext = "30298-07.htm";
						break;
					
					case Obi:
						if (cond == 8)
							htmltext = "32052-01.htm";
						else if (cond == 9)
							htmltext = "32052-06a.htm";
						else if (cond == 11)
							htmltext = "32052-07.htm";
						break;
					
					case Hitchi:
						if (cond == 9)
							htmltext = "31963-01.htm";
						else if (cond == 10)
							htmltext = "31963-04.htm";
						else if (cond == 11)
							htmltext = "31963-06a.htm";
						else if (cond == 12)
							htmltext = "31963-08.htm";
						break;
					
					case Lockirin:
						if (cond == 10)
							htmltext = "30531-01.htm";
						break;
					
					case Rydel:
						if (cond == 12)
							htmltext = "31956-01.htm";
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
			case 20389:
				if (st.getInt("cond") == 1 && st.dropItems(BoogleRatmanTooth, 1, 10, 700000))
					if (st.getQuestItemsCount(BoogleRatmanLeadersTooth) == 2)
						st.set("cond", "2");
				break;
			
			case 20390:
				if (st.getInt("cond") == 1 && st.dropItems(BoogleRatmanLeadersTooth, 1, 2, 500000))
					if (st.getQuestItemsCount(BoogleRatmanTooth) == 10)
						st.set("cond", "2");
				break;
			
			case 20017:
				if (st.getInt("cond") == 5 && st.dropItems(StolenSecretBox, 1, 1, 200000))
					st.set("cond", "6");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q418_PathToAnArtisan(418, qn, "Path to an Artisan");
	}
}