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
package quests.Q348_AnArrogantSearch;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q348_AnArrogantSearch extends Quest
{
	private static final String qn = "Q348_AnArrogantSearch";
	
	// Items
	private static final int TitansPowerstone = 4287;
	private static final int Hanellins1stLetter = 4288;
	private static final int Hanellins2ndLetter = 4289;
	private static final int Hanellins3rdLetter = 4290;
	private static final int FirstKeyOfArk = 4291;
	private static final int SecondKeyOfArk = 4292;
	private static final int ThirdKeyOfArk = 4293;
	private static final int BookOfSaint = 4397;
	private static final int BloodOfSaint = 4398;
	private static final int BoughOfSaint = 4399;
	private static final int WhiteFabricPlatinumTribe = 4294;
	private static final int WhiteFabricAngels = 5232;
	private static final int BloodedFabric = 4295;
	
	private static final int Antidote = 1831;
	private static final int HealingPotion = 1061;
	
	// NPCs
	private static final int Hanellin = 30864;
	private static final int ClaudiaAthebalt = 31001;
	private static final int Martien = 30645;
	private static final int Harne = 30144;
	private static final int ArkGuardiansCorpse = 30980;
	private static final int HolyArkOfSecrecy1 = 30977;
	private static final int HolyArkOfSecrecy2 = 30978;
	private static final int HolyArkOfSecrecy3 = 30979;
	private static final int GustavAthebaldt = 30760;
	private static final int Hardin = 30832;
	private static final int IasonHeine = 30969;
	
	// Monsters
	private static final int LesserGiantMage = 20657;
	private static final int LesserGiantElder = 20658;
	private static final int PlatinumTribeShaman = 20828;
	private static final int PlatinumTribeOverlord = 20829;
	private static final int GuardianAngel = 20859;
	private static final int SealAngel = 20860;
	
	// Quest Monsters
	private static final int AngelKiller = 27184;
	private static final int ArkGuardianElberoth = 27182;
	private static final int ArkGuardianShadowFang = 27183;
	
	// NPCs instances, in order to avoid infinite instances creation speaking to chests.
	private L2Npc _elberoth;
	private L2Npc _shadowFang;
	private L2Npc _angelKiller;
	
	public Q348_AnArrogantSearch(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			TitansPowerstone,
			Hanellins1stLetter,
			Hanellins2ndLetter,
			Hanellins3rdLetter,
			FirstKeyOfArk,
			SecondKeyOfArk,
			ThirdKeyOfArk,
			BookOfSaint,
			BloodOfSaint,
			BoughOfSaint,
			WhiteFabricPlatinumTribe,
			WhiteFabricAngels
		};
		
		addStartNpc(Hanellin);
		addTalkId(Hanellin, ClaudiaAthebalt, Martien, Harne, HolyArkOfSecrecy1, HolyArkOfSecrecy2, HolyArkOfSecrecy3, ArkGuardiansCorpse, GustavAthebaldt, Hardin, IasonHeine);
		
		addSpawnId(ArkGuardianElberoth, ArkGuardianShadowFang, AngelKiller);
		addAttackId(ArkGuardianElberoth, ArkGuardianShadowFang, AngelKiller, PlatinumTribeShaman, PlatinumTribeOverlord);
		
		addKillId(LesserGiantMage, LesserGiantElder, ArkGuardianElberoth, ArkGuardianShadowFang, AngelKiller, PlatinumTribeShaman, PlatinumTribeOverlord, GuardianAngel, SealAngel);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30864-05.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30864-09.htm"))
		{
			st.set("cond", "4");
			st.takeItems(TitansPowerstone, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-17.htm"))
		{
			st.set("cond", "5");
			st.giveItems(Hanellins1stLetter, 1);
			st.giveItems(Hanellins2ndLetter, 1);
			st.giveItems(Hanellins3rdLetter, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-36.htm"))
		{
			st.set("cond", "24");
			st.rewardItems(57, Rnd.get(1, 2) * 12000);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-37.htm"))
		{
			st.set("cond", "25");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-51.htm"))
		{
			st.set("cond", "26");
			st.giveItems(WhiteFabricAngels, (st.hasQuestItems(BloodedFabric)) ? 9 : 10);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-58.htm"))
		{
			st.set("cond", "27");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30864-57.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30864-56.htm"))
		{
			st.set("cond", "29");
			st.giveItems(WhiteFabricAngels, 10);
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("gustav", "0"); // st.unset doesn't work.
			st.set("hardin", "0");
			st.set("iason", "0");
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
				if (st.hasQuestItems(BloodedFabric))
				{
					htmltext = "30864-00.htm";
					st.exitQuest(true);
				}
				else
				{
					if (player.getLevel() < 60)
					{
						htmltext = "30864-01.htm";
						st.exitQuest(true);
					}
					else
						htmltext = "30864-02.htm";
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Hanellin:
						if (cond == 1)
							htmltext = "30864-02.htm";
						else if (cond == 2)
							htmltext = (!st.hasQuestItems(TitansPowerstone)) ? "30864-06.htm" : "30864-07.htm";
						else if (cond == 4)
							htmltext = "30864-09.htm";
						else if (cond > 4 && cond < 21)
							htmltext = (player.getInventory().hasAtLeastOneItem(BookOfSaint, BloodOfSaint, BoughOfSaint)) ? "30864-28.htm" : "30864-24.htm";
						else if (cond == 21)
						{
							htmltext = "30864-29.htm";
							st.set("cond", "22");
							st.takeItems(BookOfSaint, 1);
							st.takeItems(BloodOfSaint, 1);
							st.takeItems(BoughOfSaint, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 22)
						{
							if (st.hasQuestItems(WhiteFabricPlatinumTribe))
								htmltext = "30864-31.htm";
							else if (st.getQuestItemsCount(Antidote) < 5 || !st.hasQuestItems(HealingPotion))
								htmltext = "30864-30.htm";
							else
							{
								htmltext = "30864-31.htm";
								st.takeItems(Antidote, 5);
								st.takeItems(HealingPotion, 1);
								st.giveItems(WhiteFabricPlatinumTribe, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
						}
						else if (cond == 24)
							htmltext = "30864-38.htm";
						else if (cond == 25)
						{
							if (st.hasQuestItems(WhiteFabricPlatinumTribe))
								htmltext = "30864-39.htm";
							else if (st.hasQuestItems(BloodedFabric))
								htmltext = "30864-49.htm";
							// Use the only fabric on Baium, drop the quest.
							else
							{
								st.playSound(QuestState.SOUND_FINISH);
								st.exitQuest(true);
							}
						}
						else if (cond == 26)
						{
							if (st.getQuestItemsCount(BloodedFabric) + st.getQuestItemsCount(WhiteFabricAngels) < 10)
							{
								htmltext = "30864-54.htm";
								
								final int count = st.getQuestItemsCount(BloodedFabric);
								st.takeItems(BloodedFabric, -1);
								st.rewardItems(57, (1000 * count) + 4000);
								st.exitQuest(true);
							}
							else if (st.getQuestItemsCount(BloodedFabric) < 10)
								htmltext = "30864-52.htm";
							else if (st.getQuestItemsCount(BloodedFabric) >= 10)
								htmltext = "30864-53.htm";
						}
						else if (cond == 27)
						{
							if (st.getInt("gustav") + st.getInt("hardin") + st.getInt("iason") == 3)
							{
								htmltext = "30864-60.htm";
								st.set("cond", "28");
								st.rewardItems(57, 49000);
								st.playSound(QuestState.SOUND_MIDDLE);
							}
							else if (st.hasQuestItems(BloodedFabric) && st.getInt("usedonbaium") != 1)
								htmltext = "30864-59.htm";
							else
							{
								htmltext = "30864-61.htm";
								st.playSound(QuestState.SOUND_FINISH);
								st.exitQuest(true);
							}
						}
						else if (cond == 28)
							htmltext = "30864-55.htm";
						else if (cond == 29)
						{
							if (st.getQuestItemsCount(BloodedFabric) + st.getQuestItemsCount(WhiteFabricAngels) < 10)
							{
								htmltext = "30864-54.htm";
								
								final int count = st.getQuestItemsCount(BloodedFabric);
								st.takeItems(BloodedFabric, -1);
								st.rewardItems(57, 5000 * count);
								st.playSound(QuestState.SOUND_FINISH);
								st.exitQuest(true);
							}
							else if (st.getQuestItemsCount(BloodedFabric) < 10)
								htmltext = "30864-52.htm";
							else if (st.getQuestItemsCount(BloodedFabric) >= 10)
								htmltext = "30864-53.htm";
						}
						break;
					
					case GustavAthebaldt:
						if (cond == 27)
						{
							if (st.getQuestItemsCount(BloodedFabric) >= 3 && st.getInt("gustav") == 0)
							{
								st.set("gustav", "1");
								htmltext = "30760-01.htm";
								st.takeItems(BloodedFabric, 3);
							}
							else if (st.getInt("gustav") == 1)
								htmltext = "30760-02.htm";
							else
							{
								htmltext = "30760-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case Hardin:
						if (cond == 27)
						{
							if (st.hasQuestItems(BloodedFabric) && st.getInt("hardin") == 0)
							{
								st.set("hardin", "1");
								htmltext = "30832-01.htm";
								st.takeItems(BloodedFabric, 1);
							}
							else if (st.getInt("hardin") == 1)
								htmltext = "30832-02.htm";
							else
							{
								htmltext = "30832-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case IasonHeine:
						if (cond == 27)
						{
							if (st.getQuestItemsCount(BloodedFabric) >= 6 && st.getInt("iason") == 0)
							{
								st.set("iason", "1");
								htmltext = "30969-01.htm";
								st.takeItems(BloodedFabric, 6);
							}
							else if (st.getInt("iason") == 1)
								htmltext = "30969-02.htm";
							else
							{
								htmltext = "30969-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case Harne:
						if (cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BloodOfSaint))
							{
								if (st.hasQuestItems(Hanellins1stLetter))
								{
									htmltext = "30144-01.htm";
									st.set("cond", "17");
									st.playSound(QuestState.SOUND_MIDDLE);
									st.takeItems(Hanellins1stLetter, 1);
									st.addRadar(-418, 44174, -3568);
								}
								else if (!st.hasQuestItems(FirstKeyOfArk))
								{
									htmltext = "30144-03.htm";
									st.addRadar(-418, 44174, -3568);
								}
								else
									htmltext = "30144-04.htm";
							}
							else
								htmltext = "30144-05.htm";
						}
						break;
					
					case ClaudiaAthebalt:
						if (cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BookOfSaint))
							{
								if (st.hasQuestItems(Hanellins2ndLetter))
								{
									htmltext = "31001-01.htm";
									st.set("cond", "9");
									st.playSound(QuestState.SOUND_MIDDLE);
									st.takeItems(Hanellins2ndLetter, 1);
									st.addRadar(181472, 7158, -2725);
								}
								else if (!st.hasQuestItems(SecondKeyOfArk))
								{
									htmltext = "31001-03.htm";
									st.addRadar(181472, 7158, -2725);
								}
								else
									htmltext = "31001-04.htm";
							}
							else
								htmltext = "31001-05.htm";
						}
						break;
					
					case Martien:
						if (cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BoughOfSaint))
							{
								if (st.hasQuestItems(Hanellins3rdLetter))
								{
									htmltext = "30645-01.htm";
									st.set("cond", "13");
									st.playSound(QuestState.SOUND_MIDDLE);
									st.takeItems(Hanellins3rdLetter, 1);
									st.addRadar(50693, 158674, 376);
								}
								else if (!st.hasQuestItems(ThirdKeyOfArk))
								{
									htmltext = "30645-03.htm";
									st.addRadar(50693, 158674, 376);
								}
								else
									htmltext = "30645-04.htm";
							}
							else
								htmltext = "30645-05.htm";
						}
						break;
					
					case ArkGuardiansCorpse:
						if (!st.hasQuestItems(Hanellins1stLetter) && cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(FirstKeyOfArk) && !st.hasQuestItems(BloodOfSaint))
							{
								if (st.getInt("angelkiller") == 0)
								{
									htmltext = "30980-01.htm";
									if (_angelKiller == null)
										_angelKiller = addSpawn(AngelKiller, npc, false, 0, true);
									
									if (st.getInt("cond") != 18)
									{
										st.set("cond", "18");
										st.playSound(QuestState.SOUND_MIDDLE);
									}
								}
								else
								{
									htmltext = "30980-02.htm";
									st.giveItems(FirstKeyOfArk, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
									
									st.unset("angelkiller");
								}
							}
							else
								htmltext = "30980-03.htm";
						}
						break;
					
					case HolyArkOfSecrecy1:
						if (!st.hasQuestItems(Hanellins1stLetter) && cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BloodOfSaint))
							{
								if (st.hasQuestItems(FirstKeyOfArk))
								{
									htmltext = "30977-02.htm";
									st.set("cond", "20");
									st.playSound(QuestState.SOUND_MIDDLE);
									
									st.takeItems(FirstKeyOfArk, 1);
									st.giveItems(BloodOfSaint, 1);
									
									if (st.hasQuestItems(BookOfSaint) && st.hasQuestItems(BoughOfSaint))
										st.set("cond", "21");
								}
								else
									htmltext = "30977-04.htm";
							}
							else
								htmltext = "30977-03.htm";
						}
						break;
					
					case HolyArkOfSecrecy2:
						if (!st.hasQuestItems(Hanellins2ndLetter) && cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BookOfSaint))
							{
								if (!st.hasQuestItems(SecondKeyOfArk))
								{
									htmltext = "30978-01.htm";
									if (_elberoth == null)
										_elberoth = addSpawn(ArkGuardianElberoth, npc, false, 0, true);
								}
								else
								{
									htmltext = "30978-02.htm";
									st.set("cond", "12");
									st.playSound(QuestState.SOUND_MIDDLE);
									
									st.takeItems(SecondKeyOfArk, 1);
									st.giveItems(BookOfSaint, 1);
									
									if (st.hasQuestItems(BloodOfSaint) && st.hasQuestItems(BoughOfSaint))
										st.set("cond", "21");
								}
							}
							else
								htmltext = "30978-03.htm";
						}
						break;
					
					case HolyArkOfSecrecy3:
						if (!st.hasQuestItems(Hanellins3rdLetter) && cond >= 5 && cond <= 22)
						{
							if (!st.hasQuestItems(BoughOfSaint))
							{
								if (!st.hasQuestItems(ThirdKeyOfArk))
								{
									htmltext = "30979-01.htm";
									if (_shadowFang == null)
										_shadowFang = addSpawn(ArkGuardianShadowFang, npc, false, 0, true);
								}
								else
								{
									htmltext = "30979-02.htm";
									st.set("cond", "16");
									st.playSound(QuestState.SOUND_MIDDLE);
									
									st.takeItems(ThirdKeyOfArk, 1);
									st.giveItems(BoughOfSaint, 1);
									
									if (st.hasQuestItems(BloodOfSaint) && st.hasQuestItems(BookOfSaint))
										st.set("cond", "21");
								}
							}
							else
								htmltext = "30979-03.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case ArkGuardianElberoth:
				npc.broadcastNpcSay("This does not belong to you. Take your hands out!");
				break;
			
			case ArkGuardianShadowFang:
				npc.broadcastNpcSay("I don't believe it! Grrr!");
				break;
			
			case AngelKiller:
				npc.broadcastNpcSay("I have the key, do you wish to steal it?");
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		QuestState st = checkPlayerState(attacker, npc, Quest.STATE_STARTED);
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case ArkGuardianElberoth:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("...I feel very sorry, but I have taken your life.");
					npc.setScriptValue(1);
				}
				break;
			
			case ArkGuardianShadowFang:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("I will cover this mountain with your blood!");
					npc.setScriptValue(1);
				}
				break;
			
			case AngelKiller:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("Haha.. Really amusing! As for the key, search the corpse!");
					npc.setScriptValue(1);
				}
				
				if (npc.getCurrentHp() / npc.getMaxHp() < 0.50)
				{
					npc.abortAttack();
					npc.broadcastNpcSay("Can't get rid of you... Did you get the key from the corpse?");
					npc.decayMe();
					
					st.set("cond", "19");
					st.playSound(QuestState.SOUND_MIDDLE);
					
					_angelKiller = null;
					st.set("angelkiller", "1");
				}
				break;
			
			case PlatinumTribeOverlord:
			case PlatinumTribeShaman:
				if (st.getInt("cond") == 24 || st.getInt("cond") == 25)
				{
					if (Rnd.get(500) < 1 && st.hasQuestItems(WhiteFabricPlatinumTribe))
					{
						st.takeItems(WhiteFabricPlatinumTribe, 1);
						st.giveItems(BloodedFabric, 1);
						if (st.getInt("cond") != 24)
							st.playSound(QuestState.SOUND_ITEMGET);
						else
						{
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
					}
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, Quest.STATE_STARTED);
		if (st == null)
			return null;
		
		final int cond = st.getInt("cond");
		
		switch (npc.getNpcId())
		{
			case LesserGiantElder:
			case LesserGiantMage:
				if (cond == 2)
					st.dropItems(TitansPowerstone, 1, 1, 100000);
				break;
			
			case ArkGuardianElberoth:
				if (cond >= 5 && cond <= 22 && !st.hasQuestItems(SecondKeyOfArk))
				{
					st.giveItems(SecondKeyOfArk, 1);
					npc.broadcastNpcSay("Oh, dull-witted.. God, they...");
					
					st.set("cond", "11");
					st.playSound(QuestState.SOUND_MIDDLE);
					
					_elberoth = null;
				}
				break;
			
			case ArkGuardianShadowFang:
				if (cond >= 5 && cond <= 22 && !st.hasQuestItems(ThirdKeyOfArk))
				{
					st.giveItems(ThirdKeyOfArk, 1);
					npc.broadcastNpcSay("You do not know.. Seven seals are.. coughs");
					
					st.set("cond", "15");
					st.playSound(QuestState.SOUND_MIDDLE);
					
					_shadowFang = null;
				}
				break;
			
			case SealAngel:
			case GuardianAngel:
				if (cond == 26 || cond == 29)
				{
					if (Rnd.get(4) < 1 && st.hasQuestItems(WhiteFabricAngels))
					{
						st.takeItems(WhiteFabricAngels, 1);
						st.giveItems(BloodedFabric, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
			
			case AngelKiller:
				_angelKiller = null;
				break;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q348_AnArrogantSearch(348, qn, "An Arrogant Search");
	}
}