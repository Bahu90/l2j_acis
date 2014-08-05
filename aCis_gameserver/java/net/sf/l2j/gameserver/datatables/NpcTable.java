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
package net.sf.l2j.gameserver.datatables;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.L2DropData;
import net.sf.l2j.gameserver.model.L2MinionData;
import net.sf.l2j.gameserver.model.L2NpcAIData;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.templates.chars.L2NpcTemplate;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NpcTable
{
	private static Logger _log = Logger.getLogger(NpcTable.class.getName());
	
	private final TIntObjectHashMap<L2NpcTemplate> _npcs = new TIntObjectHashMap<>();
	
	public static NpcTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected NpcTable()
	{
		load();
	}
	
	public void reloadAllNpc()
	{
		_npcs.clear();
		load();
	}
	
	/**
	 * Load NPCs templates.<br>
	 * As some categories need an existing template in order to write infos, there are 2 loops :
	 * <ul>
	 * <li>The first loop creates the L2NpcTemplate with stats coming from "set" category.</li>
	 * <li>The second loop considers categories : skills, drops, teach, minions, ai.</li>
	 * </ul>
	 */
	private void load()
	{
		try
		{
			File file = new File("./data/xml/npcs/npcs.xml");
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(file);
			
			Node list = doc.getFirstChild();
			for (Node npc = list.getFirstChild(); npc != null; npc = npc.getNextSibling())
			{
				if ("npc".equalsIgnoreCase(npc.getNodeName()))
				{
					NamedNodeMap attrs = npc.getAttributes();
					
					int npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
					int templateId = attrs.getNamedItem("idTemplate") == null ? npcId : Integer.parseInt(attrs.getNamedItem("idTemplate").getNodeValue());
					
					StatsSet set = new StatsSet();
					set.set("id", npcId);
					set.set("idTemplate", templateId);
					set.set("name", attrs.getNamedItem("name").getNodeValue());
					set.set("title", attrs.getNamedItem("title").getNodeValue());
					
					// Categories : only "set" is read and stored. Others categories will come in second loop.
					for (Node cat = npc.getFirstChild(); cat != null; cat = cat.getNextSibling())
					{
						if ("set".equalsIgnoreCase(cat.getNodeName()))
						{
							attrs = cat.getAttributes();
							set.set(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("val").getNodeValue());
						}
					}
					
					// Create the template with basic infos.
					L2NpcTemplate template = new L2NpcTemplate(set);
					
					// Categories : add missing categories.
					for (Node cat = npc.getFirstChild(); cat != null; cat = cat.getNextSibling())
					{
						if ("ai".equalsIgnoreCase(cat.getNodeName()))
						{
							attrs = cat.getAttributes();
							
							L2NpcAIData npcAIDat = new L2NpcAIData();
							npcAIDat.setAi(attrs.getNamedItem("type").getNodeValue());
							npcAIDat.setSsCount(Integer.parseInt(attrs.getNamedItem("ssCount").getNodeValue()));
							npcAIDat.setSsRate(Integer.parseInt(attrs.getNamedItem("ssRate").getNodeValue()));
							npcAIDat.setSpsCount(Integer.parseInt(attrs.getNamedItem("spsCount").getNodeValue()));
							npcAIDat.setSpsRate(Integer.parseInt(attrs.getNamedItem("spsRate").getNodeValue()));
							npcAIDat.setAggro(Integer.parseInt(attrs.getNamedItem("aggro").getNodeValue()));
							
							// Verify if the parameter exists.
							if (attrs.getNamedItem("clan") != null)
							{
								npcAIDat.setClans(attrs.getNamedItem("clan").getNodeValue().split(";"));
								npcAIDat.setClanRange(Integer.parseInt(attrs.getNamedItem("clanRange").getNodeValue()));
								
								// Verify if the parameter exists.
								if (attrs.getNamedItem("ignoredIds") != null)
								{
									// Parse it under String array.
									String[] idsToIgnore = attrs.getNamedItem("ignoredIds").getNodeValue().split(";");
									if (idsToIgnore.length != 0)
									{
										// Parse it under int array, and then fill L2NpcAIData's _clanIgnore.
										int[] values = new int[idsToIgnore.length];
										for (int i = 0; i < idsToIgnore.length; i++)
											values[i] = Integer.parseInt(idsToIgnore[i]);
										
										npcAIDat.setIgnoredIds(values);
									}
								}
							}
							
							npcAIDat.setCanMove(Boolean.parseBoolean(attrs.getNamedItem("canMove").getNodeValue()));
							npcAIDat.setSeedable(Boolean.parseBoolean(attrs.getNamedItem("seedable").getNodeValue()));
							
							template.setAIData(npcAIDat);
						}
						else if ("skills".equalsIgnoreCase(cat.getNodeName()))
						{
							for (Node skillCat = cat.getFirstChild(); skillCat != null; skillCat = skillCat.getNextSibling())
							{
								if ("skill".equalsIgnoreCase(skillCat.getNodeName()))
								{
									attrs = skillCat.getAttributes();
									
									int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
									int level = Integer.parseInt(attrs.getNamedItem("level").getNodeValue());
									
									// Setup the npc's race. Don't register the skill.
									if (skillId == L2Skill.SKILL_NPC_RACE)
									{
										template.setRace(level);
										continue;
									}
									
									L2Skill npcSkill = SkillTable.getInstance().getInfo(skillId, level);
									if (npcSkill == null)
										continue;
									
									template.addSkill(npcSkill);
								}
							}
						}
						else if ("drops".equalsIgnoreCase(cat.getNodeName()))
						{
							for (Node dropCat = cat.getFirstChild(); dropCat != null; dropCat = dropCat.getNextSibling())
							{
								if ("category".equalsIgnoreCase(dropCat.getNodeName()))
								{
									attrs = dropCat.getAttributes();
									
									int category = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
									
									for (Node item = dropCat.getFirstChild(); item != null; item = item.getNextSibling())
									{
										if ("drop".equalsIgnoreCase(item.getNodeName()))
										{
											attrs = item.getAttributes();
											
											L2DropData dropDat = new L2DropData();
											dropDat.setItemId(Integer.parseInt(attrs.getNamedItem("itemid").getNodeValue()));
											dropDat.setMinDrop(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
											dropDat.setMaxDrop(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
											dropDat.setChance(Integer.parseInt(attrs.getNamedItem("chance").getNodeValue()));
											
											if (ItemTable.getInstance().getTemplate(dropDat.getItemId()) == null)
											{
												_log.warning("Droplist data for undefined itemId: " + dropDat.getItemId());
												continue;
											}
											template.addDropData(dropDat, category);
										}
									}
								}
							}
						}
						else if ("minions".equalsIgnoreCase(cat.getNodeName()))
						{
							for (Node minion = cat.getFirstChild(); minion != null; minion = minion.getNextSibling())
							{
								if ("minion".equalsIgnoreCase(minion.getNodeName()))
								{
									attrs = minion.getAttributes();
									
									L2MinionData minionDat = new L2MinionData();
									minionDat.setMinionId(Integer.parseInt(attrs.getNamedItem("id").getNodeValue()));
									minionDat.setAmountMin(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
									minionDat.setAmountMax(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
									
									template.addRaidData(minionDat);
								}
							}
						}
						else if ("teachTo".equalsIgnoreCase(cat.getNodeName()))
						{
							String[] classIds = cat.getAttributes().getNamedItem("classes").getNodeValue().split(";");
							
							for (String classId : classIds)
								template.addTeachInfo(ClassId.values()[Integer.parseInt(classId)]);
						}
					}
					
					_npcs.put(npcId, template);
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "NPCTable: Error parsing npcs.xml: ", e);
		}
		_log.config("NpcTable: Loaded " + _npcs.size() + " NPC templates.");
	}
	
	public L2NpcTemplate getTemplate(int id)
	{
		return _npcs.get(id);
	}
	
	/**
	 * @param name to search.
	 * @return the template list of NPCs for a given name.
	 */
	public L2NpcTemplate getTemplateByName(String name)
	{
		for (L2NpcTemplate npcTemplate : _npcs.values(new L2NpcTemplate[0]))
			if (npcTemplate.getName().equalsIgnoreCase(name))
				return npcTemplate;
		
		return null;
	}
	
	/**
	 * @param lvls to search.
	 * @return the template list of NPCs for a given level.
	 */
	public List<L2NpcTemplate> getAllOfLevel(int... lvls)
	{
		final List<L2NpcTemplate> list = new ArrayList<>();
		for (int lvl : lvls)
		{
			for (L2NpcTemplate npcTemplate : _npcs.values(new L2NpcTemplate[0]))
			{
				if (npcTemplate.getLevel() == lvl)
					list.add(npcTemplate);
			}
		}
		return list;
	}
	
	/**
	 * @param lvls to search.
	 * @return the template list of monsters for a given level.
	 */
	public List<L2NpcTemplate> getAllMonstersOfLevel(int... lvls)
	{
		final List<L2NpcTemplate> list = new ArrayList<>();
		for (int lvl : lvls)
		{
			for (L2NpcTemplate npcTemplate : _npcs.values(new L2NpcTemplate[0]))
			{
				if ((npcTemplate.getLevel() == lvl) && npcTemplate.isType("L2Monster"))
					list.add(npcTemplate);
			}
		}
		return list;
	}
	
	/**
	 * @param letters of all NPCs templates which its name start with.
	 * @return the template list of NPCs for a given letter.
	 */
	public List<L2NpcTemplate> getAllNpcStartingWith(String... letters)
	{
		final List<L2NpcTemplate> list = new ArrayList<>();
		for (String letter : letters)
		{
			for (L2NpcTemplate npcTemplate : _npcs.values(new L2NpcTemplate[0]))
			{
				if (npcTemplate.getName().startsWith(letter) && npcTemplate.isType("L2Npc"))
					list.add(npcTemplate);
			}
		}
		return list;
	}
	
	/**
	 * @param classTypes to search.
	 * @return the template list of NPCs for a given class.
	 */
	public List<L2NpcTemplate> getAllNpcOfClassType(String... classTypes)
	{
		final List<L2NpcTemplate> list = new ArrayList<>();
		for (String classType : classTypes)
		{
			for (L2NpcTemplate npcTemplate : _npcs.values(new L2NpcTemplate[0]))
			{
				if (npcTemplate.isType(classType))
					list.add(npcTemplate);
			}
		}
		return list;
	}
	
	public L2NpcTemplate[] getAllNpcs()
	{
		return _npcs.values(new L2NpcTemplate[0]);
	}
	
	private static class SingletonHolder
	{
		protected static final NpcTable _instance = new NpcTable();
	}
}