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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.templates.item.L2EtcItemType;

public class ItemsAutoDestroy
{
	protected static final Logger _log = Logger.getLogger(ItemsAutoDestroy.class.getName());
	protected List<L2ItemInstance> _items;
	protected static long _sleep;
	
	protected ItemsAutoDestroy()
	{
		_log.info("Initializing ItemsAutoDestroy.");
		_items = new CopyOnWriteArrayList<>();
		_sleep = Config.AUTODESTROY_ITEM_AFTER * 1000;
		
		if (_sleep == 0) // it should not happend as it is not called when AUTODESTROY_ITEM_AFTER = 0 but we never know..
			_sleep = 3600000;
		
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new CheckItemsForDestroy(), 5000, 5000);
	}
	
	public static ItemsAutoDestroy getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void addItem(L2ItemInstance item)
	{
		item.setDropTime(System.currentTimeMillis());
		_items.add(item);
	}
	
	protected class CheckItemsForDestroy extends Thread
	{
		@Override
		public void run()
		{
			if (_items.isEmpty())
				return;
			
			long curtime = System.currentTimeMillis();
			for (L2ItemInstance item : _items)
			{
				if (item == null || item.getDropTime() == 0 || item.getLocation() != L2ItemInstance.ItemLocation.VOID)
					_items.remove(item);
				else
				{
					if ((item.getItemType() == L2EtcItemType.HERB && (curtime - item.getDropTime()) > Config.HERB_AUTO_DESTROY_TIME) || ((curtime - item.getDropTime()) > _sleep))
					{
						L2World.getInstance().removeVisibleObject(item, item.getWorldRegion());
						L2World.getInstance().removeObject(item);
						_items.remove(item);
						
						if (Config.SAVE_DROPPED_ITEM)
							ItemsOnGroundManager.getInstance().removeObject(item);
					}
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsAutoDestroy _instance = new ItemsAutoDestroy();
	}
}