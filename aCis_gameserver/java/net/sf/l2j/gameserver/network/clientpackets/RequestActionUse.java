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
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.L2SummonAI;
import net.sf.l2j.gameserver.ai.NextAction;
import net.sf.l2j.gameserver.ai.NextAction.NextActionCallback;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SummonInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ChairSit;
import net.sf.l2j.gameserver.util.Util;

public final class RequestActionUse extends L2GameClientPacket
{
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_actionId = readD();
		_ctrlPressed = (readD() == 1);
		_shiftPressed = (readC() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (Config.DEBUG)
			_log.finest(activeChar.getName() + " request Action use: id " + _actionId + " 2:" + _ctrlPressed + " 3:" + _shiftPressed);
		
		// dont do anything if player is dead, or use fakedeath using another action than sit.
		if ((activeChar.isFakeDeath() && _actionId != 0) || activeChar.isDead())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// don't do anything if player is confused
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2Summon pet = activeChar.getPet();
		final L2Object target = activeChar.getTarget();
		
		if (Config.DEBUG)
			_log.info("Requested Action ID: " + String.valueOf(_actionId));
		
		switch (_actionId)
		{
			case 0:
				if (activeChar.getMountType() != 0)
					break;
				
				if (activeChar.isFakeDeath())
				{
					activeChar.stopFakeDeath(true);
					break;
				}
				
				if (activeChar.isSitting() || !activeChar.isMoving())
					useSit(activeChar, target);
				else
				{
					// Sit when arrive using next action, creating next action class.
					NextAction nextAction = new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.MOVE_TO, new NextActionCallback()
					{
						@Override
						public void doWork()
						{
							useSit(activeChar, target);
						}
					});
					
					// Binding next action to AI.
					activeChar.getAI().setNextAction(nextAction);
				}
				break;
			case 1:
				if (activeChar.isRunning())
					activeChar.setWalking();
				else
					activeChar.setRunning();
				
				if (Config.DEBUG)
					_log.fine("new move type: " + (activeChar.isRunning() ? "RUNNING" : "WALKING"));
				break;
			case 10: // Private Store - Sell
				activeChar.tryOpenPrivateSellStore(false);
				break;
			case 28: // Private Store - Buy
				activeChar.tryOpenPrivateBuyStore();
				break;
			case 15:
			case 21: // Change Movement Mode (pet follow/stop)
				if (pet != null)
				{
					// You can't order anymore your pet to stop if distance is superior to 2000.
					if (pet.getFollowStatus() && Util.calculateDistance(activeChar, pet, true) > 2000)
						return;
					
					if (!activeChar.isBetrayed())
						((L2SummonAI) pet.getAI()).notifyFollowStatusChange();
				}
				break;
			case 16:
			case 22: // Attack (pet attack)
				if (target != null && pet != null && pet != target && activeChar != target && !pet.isBetrayed())
				{
					if (pet.isAttackingDisabled())
					{
						if (pet.getAttackEndTime() > GameTimeController.getGameTicks())
							pet.getAI().setIntention(CtrlIntention.ATTACK, target);
						else
							return;
					}
					
					if (pet instanceof L2PetInstance && (pet.getLevel() - activeChar.getLevel() > 20))
					{
						activeChar.sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
						return;
					}
					
					if (activeChar.isInOlympiadMode() && !activeChar.isOlympiadStart())
					{
						// if L2PcInstance is in Olympia and the match isn't already start, send ActionFailed
						activeChar.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					
					pet.setTarget(target);
					if (target.isAutoAttackable(activeChar) || _ctrlPressed)
					{
						if (target instanceof L2DoorInstance)
						{
							if (((L2DoorInstance) target).isAttackable(activeChar) && pet.getNpcId() != L2SiegeSummonInstance.SWOOP_CANNON_ID)
								pet.getAI().setIntention(CtrlIntention.ATTACK, target);
						}
						// siege golem AI doesn't support attacking other than doors at the moment
						else if (pet.getNpcId() != L2SiegeSummonInstance.SIEGE_GOLEM_ID)
						{
							if (L2Character.isInsidePeaceZone(pet, target))
							{
								pet.setFollowStatus(false);
								pet.getAI().setIntention(CtrlIntention.FOLLOW, target);
							}
							else
								pet.getAI().setIntention(CtrlIntention.ATTACK, target);
						}
					}
					else
					{
						pet.setFollowStatus(false);
						pet.getAI().setIntention(CtrlIntention.FOLLOW, target);
					}
				}
				break;
			case 17:
			case 23: // Stop (pet - cancel action)
				if (pet != null && !pet.isMovementDisabled() && !pet.isBetrayed())
					pet.getAI().setIntention(CtrlIntention.ACTIVE, null);
				break;
			case 19: // Returns pet to control item
				if (pet != null && pet instanceof L2PetInstance)
				{
					if (pet.isDead())
						activeChar.sendPacket(SystemMessageId.DEAD_PET_CANNOT_BE_RETURNED);
					else if (pet.isBetrayed() || pet.isMovementDisabled())
						activeChar.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					else if (pet.isAttackingNow() || pet.isInCombat())
						activeChar.sendPacket(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE);
					else
					{
						// if the pet is hungry, you can't unsummon it
						if (((L2PetInstance) pet).isHungry())
							activeChar.sendPacket(SystemMessageId.YOU_CANNOT_RESTORE_HUNGRY_PETS);
						else
							pet.unSummon(activeChar);
					}
				}
				break;
			case 38: // pet mount/dismount
				activeChar.mountPlayer(pet);
				break;
			case 32: // Wild Hog Cannon - Mode Change
				// useSkill(4230);
				break;
			case 36: // Soulless - Toxic Smoke
				useSkill(4259, target);
				break;
			case 37: // Dwarven Manufacture
				activeChar.tryOpenWorkshop(true);
				break;
			case 39: // Soulless - Parasite Burst
				useSkill(4138, target);
				break;
			case 41: // Wild Hog Cannon - Attack
				if (target instanceof L2DoorInstance)
					useSkill(4230, target);
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				break;
			case 42: // Kai the Cat - Self Damage Shield
				useSkill(4378, activeChar);
				break;
			case 43: // Unicorn Merrow - Hydro Screw
				useSkill(4137, target);
				break;
			case 44: // Big Boom - Boom Attack
				useSkill(4139, target);
				break;
			case 45: // Unicorn Boxer - Master Recharge
				useSkill(4025, activeChar);
				break;
			case 46: // Mew the Cat - Mega Storm Strike
				useSkill(4261, target);
				break;
			case 47: // Silhouette - Steal Blood
				useSkill(4260, target);
				break;
			case 48: // Mechanic Golem - Mech. Cannon
				useSkill(4068, target);
				break;
			case 51: // General Manufacture
				activeChar.tryOpenWorkshop(false);
				break;
			case 52: // Unsummon a servitor
				if (pet != null && pet instanceof L2SummonInstance)
				{
					if (pet.isDead())
						activeChar.sendPacket(SystemMessageId.DEAD_PET_CANNOT_BE_RETURNED);
					else if (pet.isBetrayed() || pet.isMovementDisabled())
						activeChar.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					else if (pet.isAttackingNow() || pet.isInCombat())
						activeChar.sendPacket(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE);
					else
						pet.unSummon(activeChar);
				}
				break;
			case 53: // move to target
			case 54: // move to target hatch/strider
				if (target != null && pet != null && pet != target && !pet.isMovementDisabled() && !pet.isBetrayed())
				{
					pet.setFollowStatus(false);
					pet.getAI().setIntention(CtrlIntention.MOVE_TO, new L2CharPosition(target.getX(), target.getY(), target.getZ(), 0));
				}
				break;
			case 61: // Private Store Package Sell
				activeChar.tryOpenPrivateSellStore(true);
			case 1000: // Siege Golem - Siege Hammer
				if (target instanceof L2DoorInstance)
					useSkill(4079, target);
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				break;
			case 1001: // Sin Eater - Ultimate Bombastic Buster
				// useSkill();
				break;
			case 1003: // Wind Hatchling/Strider - Wild Stun
				useSkill(4710, target);
				break;
			case 1004: // Wind Hatchling/Strider - Wild Defense
				useSkill(4711, activeChar);
				break;
			case 1005: // Star Hatchling/Strider - Bright Burst
				useSkill(4712, target);
				break;
			case 1006: // Star Hatchling/Strider - Bright Heal
				useSkill(4713, activeChar);
				break;
			case 1007: // Cat Queen - Blessing of Queen
				useSkill(4699, activeChar);
				break;
			case 1008: // Cat Queen - Gift of Queen
				useSkill(4700, activeChar);
				break;
			case 1009: // Cat Queen - Cure of Queen
				useSkill(4701, target);
				break;
			case 1010: // Unicorn Seraphim - Blessing of Seraphim
				useSkill(4702, activeChar);
				break;
			case 1011: // Unicorn Seraphim - Gift of Seraphim
				useSkill(4703, activeChar);
				break;
			case 1012: // Unicorn Seraphim - Cure of Seraphim
				useSkill(4704, target);
				break;
			case 1013: // Nightshade - Curse of Shade
				useSkill(4705, target);
				break;
			case 1014: // Nightshade - Mass Curse of Shade
				useSkill(4706, activeChar);
				break;
			case 1015: // Nightshade - Shade Sacrifice
				useSkill(4707, target);
				break;
			case 1016: // Cursed Man - Cursed Blow
				useSkill(4709, target);
				break;
			case 1017: // Cursed Man - Cursed Strike/Stun
				useSkill(4708, target);
				break;
			case 1031: // Feline King - Slash
				useSkill(5135, target);
				break;
			case 1032: // Feline King - Spinning Slash
				useSkill(5136, target);
				break;
			case 1033: // Feline King - Grip of the Cat
				useSkill(5137, target);
				break;
			case 1034: // Magnus the Unicorn - Whiplash
				useSkill(5138, target);
				break;
			case 1035: // Magnus the Unicorn - Tridal Wave
				useSkill(5139, target);
				break;
			case 1036: // Spectral Lord - Corpse Kaboom
				useSkill(5142, target);
				break;
			case 1037: // Spectral Lord - Dicing Death
				useSkill(5141, target);
				break;
			case 1038: // Spectral Lord - Force Curse
				useSkill(5140, target);
				break;
			case 1039: // Swoop Cannon - Cannon Fodder
				if (!(target instanceof L2DoorInstance))
					useSkill(5110, target);
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				break;
			case 1040: // Swoop Cannon - Big Bang
				if (!(target instanceof L2DoorInstance))
					useSkill(5111, target);
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				break;
			default:
				_log.warning(activeChar.getName() + ": unhandled action type " + _actionId);
		}
	}
	
	public static boolean useSit(L2PcInstance activeChar, L2Object target)
	{
		if (activeChar.getMountType() != 0)
			return false;
		
		if (target != null && !activeChar.isSitting() && target instanceof L2StaticObjectInstance && ((L2StaticObjectInstance) target).getType() == 1 && CastleManager.getInstance().getCastle(target) != null && activeChar.isInsideRadius(target, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
		{
			final ChairSit cs = new ChairSit(activeChar, ((L2StaticObjectInstance) target).getStaticObjectId());
			activeChar.sendPacket(cs);
			activeChar.sitDown();
			activeChar.broadcastPacket(cs);
			return false;
		}
		
		if (activeChar.isSitting())
			activeChar.standUp();
		else
			activeChar.sitDown();
		
		if (Config.DEBUG)
			_log.fine("new wait type: " + (activeChar.isSitting() ? "SITTING" : "STANDING"));
		
		return true;
	}
	
	/**
	 * Cast a skill for active pet/servitor.
	 * @param skillId The id of the skill to launch.
	 * @param target The target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 */
	private void useSkill(int skillId, L2Object target)
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null || activeChar.isInStoreMode())
			return;
		
		final L2Summon activeSummon = activeChar.getPet();
		if (activeSummon != null && !activeSummon.isBetrayed())
		{
			if (activeSummon instanceof L2PetInstance)
			{
				if (activeSummon.getLevel() - activeChar.getLevel() > 20)
				{
					activeChar.sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
					return;
				}
			}
			
			final L2Skill skill = activeSummon.getTemplate().getSkills().get(skillId);
			if (skill == null)
				return;
			
			if (skill.isOffensive() && activeChar == target)
				return;
			
			activeSummon.setTarget(target);
			activeSummon.useMagic(skill, _ctrlPressed, _shiftPressed);
		}
	}
}