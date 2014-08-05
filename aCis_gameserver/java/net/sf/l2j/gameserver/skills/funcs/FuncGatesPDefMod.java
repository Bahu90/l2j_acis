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
package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.SevenSigns;
import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @author UnAfraid
 */
public class FuncGatesPDefMod extends Func
{
	private static final FuncGatesPDefMod _fmm_instance = new FuncGatesPDefMod();
	
	public static Func getInstance()
	{
		return _fmm_instance;
	}
	
	private FuncGatesPDefMod()
	{
		super(Stats.POWER_DEFENCE, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final int sealOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE);
		if (sealOwner == SevenSigns.CABAL_DAWN)
			env.value *= 1.2;
		else if (sealOwner == SevenSigns.CABAL_DUSK)
			env.value *= 0.3;
	}
}