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
package net.sf.l2j.gameserver.model.zone.type;

import java.util.concurrent.Future;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.skills.Stats;

/**
 * A damage zone
 * @author durgus
 */
public class L2DamageZone extends L2ZoneType
{
	private int _damageHPPerSec;
	private Future<?> _task;
	
	private int _castleId;
	private Castle _castle;
	
	private int _startTask;
	private int _reuseTask;
	
	private String _target = "L2Playable"; // default only playable
	
	public L2DamageZone(int id)
	{
		super(id);
		
		_damageHPPerSec = 100; // setup default damage
		
		// Setup default start / reuse time
		_startTask = 10;
		_reuseTask = 5000;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dmgSec"))
			_damageHPPerSec = Integer.parseInt(value);
		else if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("initialDelay"))
			_startTask = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("reuse"))
			_reuseTask = Integer.parseInt(value);
		else if (name.equals("targetClass"))
			_target = value;
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected boolean isAffected(L2Character character)
	{
		// check obj class
		try
		{
			if (!(Class.forName("net.sf.l2j.gameserver.model.actor." + _target).isInstance(character)))
				return false;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (_task == null && _damageHPPerSec != 0)
		{
			L2PcInstance player = character.getActingPlayer();
			
			// Castle zone, siege and no defender
			if (getCastle() != null)
				if (!(getCastle().getSiege().getIsInProgress() && player != null && player.getSiegeState() != 2))
					return;
			
			synchronized (this)
			{
				if (_task == null)
					_task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ApplyDamage(this), _startTask, _reuseTask);
			}
		}
		
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneId.DANGER_AREA, true);
			character.sendPacket(new EtcStatusUpdate((L2PcInstance) character));
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (_characterList.isEmpty() && _task != null)
			stopTask();
		
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneId.DANGER_AREA, false);
			if (!character.isInsideZone(ZoneId.DANGER_AREA))
				character.sendPacket(new EtcStatusUpdate((L2PcInstance) character));
		}
	}
	
	protected int getHPDamagePerSecond()
	{
		return _damageHPPerSec;
	}
	
	protected void stopTask()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
	}
	
	protected Castle getCastle()
	{
		if (_castleId > 0 && _castle == null)
			_castle = CastleManager.getInstance().getCastleById(_castleId);
		
		return _castle;
	}
	
	class ApplyDamage implements Runnable
	{
		private final L2DamageZone _dmgZone;
		private final Castle _castleZone;
		
		ApplyDamage(L2DamageZone zone)
		{
			_dmgZone = zone;
			_castleZone = zone.getCastle();
		}
		
		@Override
		public void run()
		{
			boolean siege = false;
			
			if (_castleZone != null)
			{
				// castle zones active only during siege
				siege = _castleZone.getSiege().getIsInProgress();
				if (!siege)
				{
					_dmgZone.stopTask();
					return;
				}
			}
			
			for (L2Character temp : _dmgZone.getCharactersInside())
			{
				if (temp != null && !temp.isDead())
				{
					if (siege)
					{
						// during siege defenders not affected
						final L2PcInstance player = temp.getActingPlayer();
						if (player != null && player.isInSiege() && player.getSiegeState() == 2)
							continue;
					}
					
					double multiplier = 1 + (temp.calcStat(Stats.DAMAGE_ZONE_VULN, 0, null, null) / 100);
					if (getHPDamagePerSecond() != 0)
						temp.reduceCurrentHp(_dmgZone.getHPDamagePerSecond() * multiplier, null, null);
				}
			}
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
}