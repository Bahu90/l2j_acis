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

import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.gameserver.skills.basefuncs.Func;
import net.sf.l2j.gameserver.templates.chars.L2PcTemplate;

public class FuncMaxCpAdd extends Func
{
	static final FuncMaxCpAdd _fmca_instance = new FuncMaxCpAdd();
	
	public static Func getInstance()
	{
		return _fmca_instance;
	}
	
	private FuncMaxCpAdd()
	{
		super(Stats.MAX_CP, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
		int lvl = env.player.getLevel() - t.classBaseLevel;
		
		// This is to prevent Players having only 1 CP
		if (lvl < 0)
			lvl = 0;
		
		double cpmod = t.lvlCpMod * lvl;
		double cpmax = (t.lvlCpAdd + cpmod) * lvl;
		double cpmin = (t.lvlCpAdd * lvl) + cpmod;
		env.value += (cpmax + cpmin) / 2;
	}
}