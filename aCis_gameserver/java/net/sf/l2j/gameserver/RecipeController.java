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
package net.sf.l2j.gameserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2ManufactureItem;
import net.sf.l2j.gameserver.model.L2RecipeInstance;
import net.sf.l2j.gameserver.model.L2RecipeList;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.RecipeBookItemList;
import net.sf.l2j.gameserver.network.serverpackets.RecipeItemMakeInfo;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;
import net.sf.l2j.util.Rnd;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class RecipeController
{
	protected static final Logger _log = Logger.getLogger(RecipeController.class.getName());
	
	private final Map<Integer, L2RecipeList> _lists = new HashMap<>();
	
	public static RecipeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected RecipeController()
	{
		try
		{
			loadFromXML();
			_log.info("RecipeController: Loaded " + _lists.size() + " recipes.");
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "RecipeController: Failed loading recipe list", e);
		}
	}
	
	public L2RecipeList getRecipeList(int listId)
	{
		return _lists.get(listId);
	}
	
	public L2RecipeList getRecipeByItemId(int itemId)
	{
		for (L2RecipeList find : _lists.values())
		{
			if (find.getRecipeId() == itemId)
				return find;
		}
		return null;
	}
	
	public void requestBookOpen(L2PcInstance player, boolean isDwarvenCraft)
	{
		RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
		response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
		player.sendPacket(response);
	}
	
	public void requestManufactureItem(L2PcInstance manufacturer, int recipeListId, L2PcInstance player)
	{
		final L2RecipeList recipeList = getValidRecipeList(player, recipeListId);
		if (recipeList == null)
			return;
		
		List<L2RecipeList> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		final RecipeItemMaker maker = new RecipeItemMaker(manufacturer, recipeList, player);
		if (maker._isValid)
			maker.run();
	}
	
	public void requestMakeItem(L2PcInstance player, int recipeListId)
	{
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player) || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		final L2RecipeList recipeList = getValidRecipeList(player, recipeListId);
		if (recipeList == null)
			return;
		
		List<L2RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		final RecipeItemMaker maker = new RecipeItemMaker(player, recipeList, player);
		if (maker._isValid)
			maker.run();
	}
	
	private void loadFromXML() throws Exception
	{
		File file = new File("./data/xml/recipes.xml");
		final Document doc = XMLDocumentFactory.getInstance().loadDocument(file);
		
		List<L2RecipeInstance> recipePartList = new ArrayList<>();
		
		Node n = doc.getFirstChild();
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if ("item".equalsIgnoreCase(d.getNodeName()))
			{
				recipePartList.clear();
				NamedNodeMap attrs = d.getAttributes();
				
				Node att = attrs.getNamedItem("id");
				if (att == null)
				{
					_log.severe("Missing id for recipe item, skipping");
					continue;
				}
				int id = Integer.parseInt(att.getNodeValue());
				
				att = attrs.getNamedItem("name");
				if (att == null)
				{
					_log.severe("Missing name for recipe item id: " + id + ", skipping");
					continue;
				}
				String recipeName = att.getNodeValue();
				
				int recipeId = -1;
				int level = -1;
				boolean isDwarvenRecipe = true;
				int mpCost = -1;
				int successRate = -1;
				int prodId = -1;
				int count = -1;
				
				for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
				{
					if ("recipe".equalsIgnoreCase(c.getNodeName()))
					{
						recipeId = Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue());
						level = Integer.parseInt(c.getAttributes().getNamedItem("level").getNodeValue());
						isDwarvenRecipe = c.getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("dwarven");
					}
					else if ("mpCost".equalsIgnoreCase(c.getNodeName()))
					{
						mpCost = Integer.parseInt(c.getTextContent());
					}
					else if ("successRate".equalsIgnoreCase(c.getNodeName()))
					{
						successRate = Integer.parseInt(c.getTextContent());
					}
					else if ("ingredient".equalsIgnoreCase(c.getNodeName()))
					{
						int ingId = Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue());
						int ingCount = Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue());
						recipePartList.add(new L2RecipeInstance(ingId, ingCount));
					}
					else if ("production".equalsIgnoreCase(c.getNodeName()))
					{
						prodId = Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue());
						count = Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue());
					}
				}
				
				L2RecipeList recipeList = new L2RecipeList(id, level, recipeId, recipeName, successRate, mpCost, prodId, count, isDwarvenRecipe);
				for (L2RecipeInstance recipePart : recipePartList)
					recipeList.addRecipe(recipePart);
				
				_lists.put(id, recipeList);
			}
		}
	}
	
	private class RecipeItemMaker implements Runnable
	{
		protected boolean _isValid;
		protected final L2RecipeList _recipeList;
		protected final L2PcInstance _player; // "crafter"
		protected final L2PcInstance _target; // "customer"
		protected final int _skillId;
		protected final int _skillLevel;
		protected double _manaRequired;
		protected int _price;
		
		public RecipeItemMaker(L2PcInstance pPlayer, L2RecipeList pRecipeList, L2PcInstance pTarget)
		{
			_player = pPlayer;
			_target = pTarget;
			_recipeList = pRecipeList;
			
			_isValid = false;
			_skillId = _recipeList.isDwarvenRecipe() ? L2Skill.SKILL_CREATE_DWARVEN : L2Skill.SKILL_CREATE_COMMON;
			_skillLevel = _player.getSkillLevel(_skillId);
			
			_manaRequired = _recipeList.getMpCost();
			
			_player.isInCraftMode(true);
			
			if (_player.isAlikeDead() || _target.isAlikeDead())
			{
				_player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (_player.isProcessingTransaction() || _target.isProcessingTransaction())
			{
				_target.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// validate recipe list
			if (_recipeList.getRecipes().length == 0)
			{
				_player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// validate skill level
			if (_recipeList.getLevel() > _skillLevel)
			{
				_player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// check that customer can afford to pay for creation services
			if (_player != _target)
			{
				for (L2ManufactureItem temp : _player.getCreateList().getList())
				{
					if (temp.getRecipeId() == _recipeList.getId()) // find recipe for item we want manufactured
					{
						_price = temp.getCost();
						if (_target.getAdena() < _price) // check price
						{
							_target.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
							abort();
							return;
						}
						break;
					}
				}
			}
			
			// Check if inventory got all required materials.
			if (!listItems(false))
			{
				abort();
				return;
			}
			
			// initial mana check requires MP as written on recipe
			if (_player.getCurrentMp() < _manaRequired)
			{
				_target.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
				abort();
				return;
			}
			
			updateMakeInfo(true);
			updateCurMp();
			updateCurLoad();
			
			_player.isInCraftMode(false);
			_isValid = true;
		}
		
		@Override
		public void run()
		{
			if (!Config.IS_CRAFTING_ENABLED)
			{
				_target.sendMessage("Item creation is currently disabled.");
				abort();
				return;
			}
			
			if (_player == null || _target == null)
			{
				_log.warning("Player or target == null (disconnected?), aborting" + _target + _player);
				abort();
				return;
			}
			
			if (!_player.isOnline() || !_target.isOnline())
			{
				_log.warning("Player or target is not online, aborting " + _target + _player);
				abort();
				return;
			}
			
			_player.reduceCurrentMp(_manaRequired);
			
			// first take adena for manufacture
			if (_target != _player && _price > 0) // customer must pay for services
			{
				// attempt to pay for item
				L2ItemInstance adenatransfer = _target.transferItem("PayManufacture", _target.getInventory().getAdenaInstance().getObjectId(), _price, _player.getInventory(), _player);
				if (adenatransfer == null)
				{
					_target.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
					abort();
					return;
				}
			}
			
			// Inventory check failed.
			if (!listItems(true))
			{
				abort();
				return;
			}
			
			if (Rnd.get(100) < _recipeList.getSuccessRate())
			{
				rewardPlayer(); // and immediately puts created item in its place
				updateMakeInfo(true);
			}
			else
			{
				if (_target != _player)
				{
					SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CREATION_OF_S2_FOR_S1_AT_S3_ADENA_FAILED);
					msg.addPcName(_target);
					msg.addItemName(_recipeList.getItemId());
					msg.addItemNumber(_price);
					_player.sendPacket(msg);
					
					msg = SystemMessage.getSystemMessage(SystemMessageId.S1_FAILED_TO_CREATE_S2_FOR_S3_ADENA);
					msg.addPcName(_player);
					msg.addItemName(_recipeList.getItemId());
					msg.addItemNumber(_price);
					_target.sendPacket(msg);
				}
				else
					_target.sendPacket(SystemMessageId.ITEM_MIXING_FAILED);
				
				updateMakeInfo(false);
			}
			// update load and mana bar of craft window
			updateCurMp();
			updateCurLoad();
			_player.isInCraftMode(false);
			_target.sendPacket(new ItemList(_target, false));
		}
		
		private void updateMakeInfo(boolean success)
		{
			if (_target == _player)
				_target.sendPacket(new RecipeItemMakeInfo(_recipeList.getId(), _target, success));
			else
				_target.sendPacket(new RecipeShopItemInfo(_player, _recipeList.getId()));
		}
		
		private void updateCurLoad()
		{
			StatusUpdate su = new StatusUpdate(_target);
			su.addAttribute(StatusUpdate.CUR_LOAD, _target.getCurrentLoad());
			_target.sendPacket(su);
		}
		
		private void updateCurMp()
		{
			StatusUpdate su = new StatusUpdate(_target);
			su.addAttribute(StatusUpdate.CUR_MP, (int) _target.getCurrentMp());
			_target.sendPacket(su);
		}
		
		@SuppressWarnings("null")
		private boolean listItems(boolean remove)
		{
			final L2RecipeInstance[] recipes = _recipeList.getRecipes();
			final Inventory inv = _target.getInventory();
			
			List<int[]> materials = new ArrayList<>();
			
			for (L2RecipeInstance recipe : recipes)
			{
				int quantity = _recipeList.isConsumable() ? (int) (recipe.getQuantity() * Config.RATE_CONSUMABLE_COST) : (int) recipe.getQuantity();
				if (quantity > 0)
				{
					L2ItemInstance item = inv.getItemByItemId(recipe.getItemId());
					int itemQuantityAmount = item == null ? 0 : item.getCount();
					
					// check materials
					if (itemQuantityAmount < quantity)
					{
						_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MISSING_S2_S1_TO_CREATE).addItemName(recipe.getItemId()).addItemNumber(quantity - itemQuantityAmount));
						abort();
						return false;
					}
					
					materials.add(new int[]
					{
						item.getItemId(),
						quantity
					});
				}
			}
			
			if (remove)
			{
				for (int[] tmp : materials)
				{
					inv.destroyItemByItemId("Manufacture", tmp[0], tmp[1], _target, _player);
					
					if (tmp[1] > 1)
						_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(tmp[0]).addItemNumber(tmp[1]));
					else
						_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(tmp[0]));
				}
			}
			return true;
		}
		
		private void abort()
		{
			updateMakeInfo(false);
			_player.isInCraftMode(false);
		}
		
		private void rewardPlayer()
		{
			int itemId = _recipeList.getItemId();
			int itemCount = _recipeList.getCount();
			
			_target.getInventory().addItem("Manufacture", itemId, itemCount, _target, _player);
			
			// inform customer of earned item
			if (_target != _player)
			{
				// inform manufacturer of earned profit
				if (itemCount == 1)
				{
					_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_CREATED_FOR_S1_FOR_S3_ADENA).addString(_target.getName()).addItemName(itemId).addItemNumber(_price));
					_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CREATED_S2_FOR_S3_ADENA).addString(_player.getName()).addItemName(itemId).addItemNumber(_price));
				}
				else
				{
					_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S3_S_CREATED_FOR_S1_FOR_S4_ADENA).addString(_target.getName()).addNumber(itemCount).addItemName(itemId).addItemNumber(_price));
					_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CREATED_S2_S3_S_FOR_S4_ADENA).addString(_player.getName()).addNumber(itemCount).addItemName(itemId).addItemNumber(_price));
				}
			}
			
			if (itemCount > 1)
				_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(itemId).addNumber(itemCount));
			else
				_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(itemId));
			
			updateMakeInfo(true); // success
		}
	}
	
	private L2RecipeList getValidRecipeList(L2PcInstance player, int id)
	{
		final L2RecipeList recipeList = _lists.get(id);
		if (recipeList == null || recipeList.getRecipes().length == 0)
		{
			player.sendMessage("No recipe for: " + id);
			player.isInCraftMode(false);
			return null;
		}
		return recipeList;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeController _instance = new RecipeController();
	}
}