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
package net.sf.l2j.gameserver.instancemanager;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class SiegeManager
{
	private static final Logger _log = Logger.getLogger(SiegeManager.class.getName());
	
	public static final SiegeManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	// Data Field
	private int _attackerMaxClans = 10; // Max number of clans
	private int _attackerRespawnDelay = 10000; // Time in ms.
	private int _defenderMaxClans = 10; // Max number of clans
	
	// Siege settings
	private TIntObjectHashMap<List<SiegeSpawn>> _artefactSpawnList;
	private TIntObjectHashMap<List<SiegeSpawn>> _controlTowerSpawnList;
	private TIntObjectHashMap<List<SiegeSpawn>> _flameTowerSpawnList;
	
	private int _flagMaxCount = 1;
	private int _siegeClanMinLevel = 4;
	private int _siegeLength = 120; // Time in minute.
	
	protected SiegeManager()
	{
		try (InputStream is = new FileInputStream(new File(Config.SIEGE_FILE)))
		{
			Properties siegeSettings = new Properties();
			siegeSettings.load(is);
			
			// Siege settings
			_attackerMaxClans = Integer.decode(siegeSettings.getProperty("AttackerMaxClans", "10"));
			_attackerRespawnDelay = Integer.decode(siegeSettings.getProperty("AttackerRespawn", "10000"));
			_defenderMaxClans = Integer.decode(siegeSettings.getProperty("DefenderMaxClans", "10"));
			_flagMaxCount = Integer.decode(siegeSettings.getProperty("MaxFlags", "1"));
			_siegeClanMinLevel = Integer.decode(siegeSettings.getProperty("SiegeClanMinLevel", "4"));
			_siegeLength = Integer.decode(siegeSettings.getProperty("SiegeLength", "120"));
			
			// Siege spawns settings
			_controlTowerSpawnList = new TIntObjectHashMap<>();
			_artefactSpawnList = new TIntObjectHashMap<>();
			_flameTowerSpawnList = new TIntObjectHashMap<>();
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				List<SiegeSpawn> _controlTowersSpawns = new ArrayList<>();
				
				for (int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty(castle.getName() + "ControlTower" + Integer.toString(i), "");
					
					if (_spawnParams.isEmpty())
						break;
					
					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");
					
					try
					{
						int x = Integer.parseInt(st.nextToken());
						int y = Integer.parseInt(st.nextToken());
						int z = Integer.parseInt(st.nextToken());
						int npc_id = Integer.parseInt(st.nextToken());
						int hp = Integer.parseInt(st.nextToken());
						
						_controlTowersSpawns.add(new SiegeSpawn(castle.getCastleId(), x, y, z, 0, npc_id, hp));
					}
					catch (Exception e)
					{
						_log.warning("Error while loading control tower(s) for " + castle.getName() + " castle.");
					}
				}
				
				List<SiegeSpawn> _flameTowersSpawns = new ArrayList<>();
				
				for (int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty(castle.getName() + "FlameTower" + Integer.toString(i), "");
					
					if (_spawnParams.isEmpty())
						break;
					
					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");
					
					try
					{
						int x = Integer.parseInt(st.nextToken());
						int y = Integer.parseInt(st.nextToken());
						int z = Integer.parseInt(st.nextToken());
						int npc_id = Integer.parseInt(st.nextToken());
						int hp = Integer.parseInt(st.nextToken());
						
						_flameTowersSpawns.add(new SiegeSpawn(castle.getCastleId(), x, y, z, 0, npc_id, hp));
					}
					catch (Exception e)
					{
						_log.warning("Error while loading flame tower(s) for " + castle.getName() + " castle.");
					}
				}
				
				List<SiegeSpawn> _artefactSpawns = new ArrayList<>();
				
				for (int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty(castle.getName() + "Artefact" + Integer.toString(i), "");
					
					if (_spawnParams.isEmpty())
						break;
					
					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");
					
					try
					{
						int x = Integer.parseInt(st.nextToken());
						int y = Integer.parseInt(st.nextToken());
						int z = Integer.parseInt(st.nextToken());
						int heading = Integer.parseInt(st.nextToken());
						int npc_id = Integer.parseInt(st.nextToken());
						
						_artefactSpawns.add(new SiegeSpawn(castle.getCastleId(), x, y, z, heading, npc_id));
					}
					catch (Exception e)
					{
						_log.warning("Error while loading artefact(s) for " + castle.getName() + " castle.");
					}
				}
				
				_controlTowerSpawnList.put(castle.getCastleId(), _controlTowersSpawns);
				_artefactSpawnList.put(castle.getCastleId(), _artefactSpawns);
				_flameTowerSpawnList.put(castle.getCastleId(), _flameTowersSpawns);
			}
			
		}
		catch (Exception e)
		{
			_log.warning("Error while loading siege data: " + e);
		}
	}
	
	/**
	 * That method verify if the player can summon a siege summon. Following checks are made :
	 * <UL>
	 * <LI>must be on a castle ground;</LI>
	 * <LI>during a siege period;</LI>
	 * <LI>must be an attacker;</LI>
	 * <LI>mustn't be inside a castle (siege zone, but not castle zone)</LI>
	 * </UL>
	 * @param activeChar The player who attempt to summon a siege summon.
	 * @return true if the player can summon, false otherwise (send an error message aswell).
	 */
	public final static boolean checkIfOkToSummon(L2PcInstance activeChar)
	{
		if (activeChar == null)
			return false;
		
		Castle castle = CastleManager.getInstance().getCastle(activeChar);
		if ((castle == null || castle.getCastleId() <= 0) || (!castle.getSiege().getIsInProgress()) || (activeChar.getClanId() != 0 && castle.getSiege().getAttackerClan(activeChar.getClanId()) == null) || (activeChar.isInSiege() && activeChar.isInsideZone(ZoneId.CASTLE)))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_CALL_PET_FROM_THIS_LOCATION));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verify if the clan is registered to any siege.
	 * @param clan The L2Clan of the player
	 * @return true if the clan is registered or owner of a castle
	 */
	public final static boolean checkIsRegistered(L2Clan clan)
	{
		if (clan == null || clan.hasCastle())
			return true;
		
		boolean register = false;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM siege_clans WHERE clan_id=?");
			statement.setInt(1, clan.getClanId());
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
			{
				register = true;
				break;
			}
			
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: checkIsRegistered(): " + e);
		}
		return register;
	}
	
	public final static void addSiegeSkills(L2PcInstance character)
	{
		for (L2Skill sk : SkillTable.getInstance().getSiegeSkills(character.isNoble()))
			character.addSkill(sk, false);
	}
	
	public final static void removeSiegeSkills(L2PcInstance character)
	{
		for (L2Skill sk : SkillTable.getInstance().getSiegeSkills(character.isNoble()))
			character.removeSkill(sk);
	}
	
	public final List<SiegeSpawn> getArtefactSpawnList(int castleId)
	{
		return _artefactSpawnList.get(castleId);
	}
	
	public final List<SiegeSpawn> getControlTowerSpawnList(int castleId)
	{
		return _controlTowerSpawnList.get(castleId);
	}
	
	public final List<SiegeSpawn> getFlameTowerSpawnList(int castleId)
	{
		return _flameTowerSpawnList.get(castleId);
	}
	
	public final int getAttackerMaxClans()
	{
		return _attackerMaxClans;
	}
	
	public final int getAttackerRespawnDelay()
	{
		return _attackerRespawnDelay;
	}
	
	public final int getDefenderMaxClans()
	{
		return _defenderMaxClans;
	}
	
	public final int getFlagMaxCount()
	{
		return _flagMaxCount;
	}
	
	public final static Siege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public final static Siege getSiege(int x, int y, int z)
	{
		for (Castle castle : CastleManager.getInstance().getCastles())
			if (castle.getSiege().checkIfInZone(x, y, z))
				return castle.getSiege();
		
		return null;
	}
	
	public final int getSiegeClanMinLevel()
	{
		return _siegeClanMinLevel;
	}
	
	public final int getSiegeLength()
	{
		return _siegeLength;
	}
	
	public final static List<Siege> getSieges()
	{
		List<Siege> sieges = new ArrayList<>();
		for (Castle castle : CastleManager.getInstance().getCastles())
			sieges.add(castle.getSiege());
		
		return sieges;
	}
	
	public static class SiegeSpawn
	{
		Location _location;
		private final int _npcId;
		private final int _heading;
		private final int _castleId;
		private int _hp;
		
		public SiegeSpawn(int castle_id, int x, int y, int z, int heading, int npc_id)
		{
			_castleId = castle_id;
			_location = new Location(x, y, z, heading);
			_heading = heading;
			_npcId = npc_id;
		}
		
		public SiegeSpawn(int castle_id, int x, int y, int z, int heading, int npc_id, int hp)
		{
			_castleId = castle_id;
			_location = new Location(x, y, z, heading);
			_heading = heading;
			_npcId = npc_id;
			_hp = hp;
		}
		
		public int getCastleId()
		{
			return _castleId;
		}
		
		public int getNpcId()
		{
			return _npcId;
		}
		
		public int getHeading()
		{
			return _heading;
		}
		
		public int getHp()
		{
			return _hp;
		}
		
		public Location getLocation()
		{
			return _location;
		}
	}
	
	private static class SingletonHolder
	{
		protected static final SiegeManager _instance = new SiegeManager();
	}
}