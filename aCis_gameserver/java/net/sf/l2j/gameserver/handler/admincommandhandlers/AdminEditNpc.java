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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.l2j.gameserver.TradeController;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2DropCategory;
import net.sf.l2j.gameserver.model.L2DropData;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2TradeList;
import net.sf.l2j.gameserver.model.L2TradeList.L2TradeItem;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestEventType;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.templates.chars.L2NpcTemplate;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.util.StringUtil;

/**
 * @author terry
 */
public class AdminEditNpc implements IAdminCommandHandler
{
	private final static int PAGE_LIMIT = 20;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_droplist",
		"admin_show_scripts",
		"admin_show_shop",
		"admin_show_shoplist",
		"admin_show_skilllist"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_show_shoplist"))
		{
			String[] args = command.split(" ");
			if (args.length > 2)
				showShopList(activeChar, Integer.parseInt(command.split(" ")[1]), Integer.parseInt(command.split(" ")[2]));
		}
		else if (command.startsWith("admin_show_shop"))
		{
			String[] args = command.split(" ");
			if (args.length > 1)
				showShop(activeChar, Integer.parseInt(command.split(" ")[1]));
		}
		else if (command.startsWith("admin_show_droplist"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				int npcId = Integer.parseInt(st.nextToken());
				int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
				
				showNpcDropList(activeChar, npcId, page);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //show_droplist <npc_id> [<page>]");
			}
		}
		else if (command.startsWith("admin_show_skilllist"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				int npcId = Integer.parseInt(st.nextToken());
				int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 0;
				
				showNpcSkillList(activeChar, npcId, page);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //show_skilllist <npc_id> <page>");
			}
		}
		else if (command.startsWith("admin_show_scripts"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				showScriptsList(activeChar, Integer.parseInt(st.nextToken()));
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //show_scripts <npc_id>");
			}
		}
		
		return true;
	}
	
	private static void showShopList(L2PcInstance activeChar, int tradeListID, int page)
	{
		L2TradeList tradeList = TradeController.getInstance().getBuyList(tradeListID);
		if (page > tradeList.getItems().size() / PAGE_LIMIT + 1 || page < 1)
			return;
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(itemListHtml(tradeList, page));
		activeChar.sendPacket(adminReply);
	}
	
	private static String itemListHtml(L2TradeList tradeList, int page)
	{
		final StringBuilder replyMSG = new StringBuilder();
		
		int max = tradeList.getItems().size() / PAGE_LIMIT;
		if (tradeList.getItems().size() > PAGE_LIMIT * max)
			max++;
		
		StringUtil.append(replyMSG, "<html><body><center><font color=\"LEVEL\">", NpcTable.getInstance().getTemplate(Integer.parseInt(tradeList.getNpcId())).getName(), " (", tradeList.getNpcId(), ") Shop ID: ", Integer.toString(tradeList.getListId()), "</font></center><table width=300 bgcolor=666666><tr>");
		
		for (int x = 0; x < max; x++)
		{
			int pagenr = x + 1;
			if (page == pagenr)
			{
				replyMSG.append("<td>Page ");
				replyMSG.append(pagenr);
				replyMSG.append("</td>");
			}
			else
			{
				replyMSG.append("<td><a action=\"bypass -h admin_show_shoplist ");
				replyMSG.append(tradeList.getListId());
				replyMSG.append(" ");
				replyMSG.append(x + 1);
				replyMSG.append("\"> Page ");
				replyMSG.append(pagenr);
				replyMSG.append(" </a></td>");
			}
		}
		
		replyMSG.append("</tr></table><table width=\"100%\"><tr><td width=200>Item</td><td width=80>Price</td></tr>");
		
		int start = ((page - 1) * PAGE_LIMIT);
		int end = Math.min(((page - 1) * PAGE_LIMIT) + PAGE_LIMIT, tradeList.getItems().size());
		
		for (L2TradeItem item : tradeList.getItems(start, end))
			StringUtil.append(replyMSG, "<tr><td>", ItemTable.getInstance().getTemplate(item.getItemId()).getName(), "</td><td>", String.valueOf(item.getPrice()), "</td></tr>");
		
		StringUtil.append(replyMSG, "</table></body></html>");
		
		return replyMSG.toString();
	}
	
	private static void showShop(L2PcInstance activeChar, int merchantID)
	{
		List<L2TradeList> tradeLists = TradeController.getInstance().getBuyListByNpcId(merchantID);
		if (tradeLists == null)
		{
			activeChar.sendMessage("Unknown npc template Id: " + merchantID);
			return;
		}
		
		final StringBuilder replyMSG = new StringBuilder();
		StringUtil.append(replyMSG, "<html><title>Merchant Shop Lists</title><body>");
		
		if (activeChar.getTarget() instanceof L2MerchantInstance)
		{
			L2Npc merchant = (L2Npc) activeChar.getTarget();
			int taxRate = merchant.getCastle().getTaxPercent();
			
			StringUtil.append(replyMSG, "<center><font color=\"LEVEL\">", merchant.getName(), " (", Integer.toString(merchantID), ")</font></center><br>Tax rate: ", Integer.toString(taxRate), "%");
		}
		
		StringUtil.append(replyMSG, "<table width=\"100%\">");
		
		for (L2TradeList tradeList : tradeLists)
		{
			if (tradeList != null)
				StringUtil.append(replyMSG, "<tr><td><a action=\"bypass -h admin_show_shoplist ", String.valueOf(tradeList.getListId()), " 1\">Merchant list ID: ", String.valueOf(tradeList.getListId()), "</a></td></tr>");
		}
		
		StringUtil.append(replyMSG, "</table></body></html>");
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private static void showNpcDropList(L2PcInstance activeChar, int npcId, int page)
	{
		L2NpcTemplate npcData = NpcTable.getInstance().getTemplate(npcId);
		if (npcData == null)
		{
			activeChar.sendMessage("Unknown npc template id " + npcId);
			return;
		}
		
		final StringBuilder replyMSG = new StringBuilder(2000);
		replyMSG.append("<html><title>Show droplist page ");
		replyMSG.append(page);
		replyMSG.append("</title><body><center><font color=\"LEVEL\">");
		replyMSG.append(npcData.getName());
		replyMSG.append(" (");
		replyMSG.append(npcId);
		replyMSG.append(")</font></center><br>");
		
		if (!npcData.getDropData().isEmpty())
		{
			replyMSG.append("Drop type legend: <font color=\"3BB9FF\">Drop</font> | <font color=\"00ff00\">Sweep</font> | <font color=\"C12869\">Quest</font><br><table><tr><td width=25>cat.</td><td width=255>item</td></tr>");
			
			int myPage = 1;
			int i = 0;
			int shown = 0;
			boolean hasMore = false;
			
			for (L2DropCategory cat : npcData.getDropData())
			{
				if (shown == PAGE_LIMIT)
				{
					hasMore = true;
					break;
				}
				
				for (L2DropData drop : cat.getAllDrops())
				{
					final String color = (drop.isQuestDrop() ? "C12869" : (cat.isSweep() ? "00FF00" : "3BB9FF"));
					
					if (myPage != page)
					{
						i++;
						if (i == PAGE_LIMIT)
						{
							myPage++;
							i = 0;
						}
						continue;
					}
					
					if (shown == PAGE_LIMIT)
					{
						hasMore = true;
						break;
					}
					
					replyMSG.append("<tr><td><font color=\"");
					replyMSG.append(color);
					replyMSG.append("\">");
					replyMSG.append(cat.getCategoryType());
					replyMSG.append("</td><td>");
					replyMSG.append(ItemTable.getInstance().getTemplate(drop.getItemId()).getName());
					replyMSG.append(" (");
					replyMSG.append(drop.getItemId());
					replyMSG.append(")</td></tr>");
					shown++;
				}
			}
			
			replyMSG.append("</table><table width=\"100%\" bgcolor=666666><tr>");
			
			if (page > 1)
			{
				replyMSG.append("<td width=120><a action=\"bypass -h admin_show_droplist ");
				replyMSG.append(npcId);
				replyMSG.append(" ");
				replyMSG.append(page - 1);
				replyMSG.append("\">Prev Page</a></td>");
				if (!hasMore)
				{
					replyMSG.append("<td width=100>Page ");
					replyMSG.append(page);
					replyMSG.append("</td><td width=70></td></tr>");
				}
			}
			
			if (hasMore)
			{
				if (page <= 1)
					replyMSG.append("<td width=120></td>");
				replyMSG.append("<td width=100>Page ");
				replyMSG.append(page);
				replyMSG.append("</td><td width=70><a action=\"bypass -h admin_show_droplist ");
				replyMSG.append(npcId);
				replyMSG.append(" ");
				replyMSG.append(page + 1);
				replyMSG.append("\">Next Page</a></td></tr>");
			}
			replyMSG.append("</table>");
		}
		else
			replyMSG.append("This NPC has no drops.");
		
		replyMSG.append("</body></html>");
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private static void showNpcSkillList(L2PcInstance activeChar, int npcId, int page)
	{
		final L2NpcTemplate npcData = NpcTable.getInstance().getTemplate(npcId);
		if (npcData == null)
		{
			activeChar.sendMessage("Template id unknown: " + npcId);
			return;
		}
		
		final L2Skill[] skills = npcData.getSkillsArray();
		final int _skillsize = skills.length;
		
		int MaxPages = _skillsize / PAGE_LIMIT;
		if (_skillsize > PAGE_LIMIT * MaxPages)
			MaxPages++;
		
		if (page > MaxPages)
			page = MaxPages;
		
		final int SkillsStart = PAGE_LIMIT * page;
		int SkillsEnd = _skillsize;
		if (SkillsEnd - SkillsStart > PAGE_LIMIT)
			SkillsEnd = SkillsStart + PAGE_LIMIT;
		
		final StringBuilder replyMSG = StringUtil.startAppend(200 + _skillsize * 100, "<html><body><center><font color=\"LEVEL\">" + npcData.getName() + " (", Integer.toString(npcId), "): ", Integer.toString(_skillsize), " skills</font></center><table width=\"100%\" bgcolor=666666><tr>");
		
		for (int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			if (page == x)
				StringUtil.append(replyMSG, "<td>Page ", Integer.toString(pagenr), "</td>");
			else
				StringUtil.append(replyMSG, "<td><a action=\"bypass -h admin_show_skilllist ", Integer.toString(npcId), " ", Integer.toString(x), "\"> Page ", Integer.toString(pagenr), " </a></td>");
		}
		replyMSG.append("</tr></table><table width=\"100%\">");
		
		for (int i = SkillsStart; i < SkillsEnd; i++)
		{
			final L2Skill skill = skills[i];
			if (skill == null)
				continue;
			
			StringUtil.append(replyMSG, "<tr><td>" + ((skill.getSkillType() == L2SkillType.NOTDONE) ? ("<font color=\"777777\">" + skill.getName() + "</font>") : skill.getName()) + " [", Integer.toString(skill.getId()), "-", Integer.toString(skill.getLevel()), "]</td></tr>");
		}
		StringUtil.append(replyMSG, "</table></body></html>");
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private static void showScriptsList(L2PcInstance activeChar, int npcId)
	{
		L2NpcTemplate npcData = NpcTable.getInstance().getTemplate(npcId);
		if (npcData == null)
		{
			activeChar.sendMessage("Unknown npc template id " + npcId);
			return;
		}
		
		final StringBuilder replyMSG = new StringBuilder(2000);
		replyMSG.append("<html><body><center><font color=\"LEVEL\">");
		replyMSG.append(npcData.getName());
		replyMSG.append(" (");
		replyMSG.append(npcId);
		replyMSG.append(")</font></center><br>");
		
		if (!npcData.getEventQuests().isEmpty())
		{
			QuestEventType type = null; // Used to see if we moved of type.
			
			// For any type of QuestEventType
			for (Map.Entry<QuestEventType, List<Quest>> entry : npcData.getEventQuests().entrySet())
			{
				if (type != entry.getKey())
				{
					type = entry.getKey();
					replyMSG.append("<br><font color=\"LEVEL\">" + type.name() + "</font><br1>");
				}
				
				for (Quest quest : entry.getValue())
					replyMSG.append(quest.getName() + "<br1>");
			}
		}
		else
			replyMSG.append("This NPC isn't affected by scripts.");
		
		replyMSG.append("</body></html>");
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}