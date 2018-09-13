/**
 * Copyright (c) 2018 Gregorius Techneticies
 *
 * This file is part of GregTech.
 *
 * GregTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GregTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GregTech. If not, see <http://www.gnu.org/licenses/>.
 */

package gregtech.api.interfaces.tileentity;

import gregapi.util.UT;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

@Deprecated
public interface IEnergyConnected extends IColoredTileEntity, IHasWorldObjectAndCoords {
	public long injectEnergyUnits(byte aSide, long aVoltage, long aAmperage);
	public boolean inputEnergyFrom(byte aSide);
	public boolean outputsEnergyTo(byte aSide);
	
	public static class Util {
		public static final long emitEnergyToNetwork(long aVoltage, long aAmperage, IEnergyConnected aEmitter) {
			long rUsedAmperes = 0;
			for (byte i = 0, j = 0; i < 6 && aAmperage > rUsedAmperes; i++) if (aEmitter.outputsEnergyTo(i)) {
				j = UT.Code.opposite(i);
				TileEntity tTileEntity = aEmitter.getTileEntityAtSide(i);
				if (tTileEntity instanceof IEnergyConnected) {
					if (aEmitter.getColorization() >= 0) {
						byte tColor = ((IEnergyConnected)tTileEntity).getColorization();
						if (tColor >= 0 && tColor != aEmitter.getColorization()) continue;
					}
					rUsedAmperes+=((IEnergyConnected)tTileEntity).injectEnergyUnits(j, aVoltage, aAmperage-rUsedAmperes);
//              } else if (tTileEntity instanceof IEnergySink) {
//                  if (((IEnergySink)tTileEntity).acceptsEnergyFrom((TileEntity)aEmitter, ForgeDirection.getOrientation(j))) {
//                      while (aAmperage > rUsedAmperes && ((IEnergySink)tTileEntity).demandedEnergyUnits() > 0 && ((IEnergySink)tTileEntity).injectEnergyUnits(ForgeDirection.getOrientation(j), aVoltage) < aVoltage) rUsedAmperes++;
//                  }
				} else if (tTileEntity instanceof IEnergySink) {
					if (((IEnergySink)tTileEntity).acceptsEnergyFrom((TileEntity)aEmitter, ForgeDirection.getOrientation(j))) {
						while (aAmperage > rUsedAmperes && ((IEnergySink)tTileEntity).getDemandedEnergy() > 0 && ((IEnergySink)tTileEntity).injectEnergy(ForgeDirection.getOrientation(j), aVoltage, aVoltage) < aVoltage) rUsedAmperes++;
					}
				}
			}
			return rUsedAmperes;
		}
	}
}
