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

package gregtech.worldgen.structure;

import static gregapi.data.CS.*;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;

/**
 * @author Gregorius Techneticies
 */
public class WorldgenStructureExteriorPillar extends WorldgenStructure {
	public static boolean generate(World aWorld, Random aRandom, int aChunkX, int aChunkZ, StructureData aData) {
		boolean temp = T;
		for (int tX = 6; tX <= 9 && temp; tX++) for (int tZ = 6; tZ <= 9 && temp; tZ++) if (aWorld.getBlock(aChunkX+tX, aData.mOffsetY-1, aChunkZ+tZ).isOpaqueCube()) temp = F;
		
		if (temp) for (int tX =  5; tX <= 10; tX++) for (int tZ =  5; tZ <= 10; tZ++) {
			setSmoothBlock (aWorld, aChunkX+tX, aData.mOffsetY-1, aChunkZ+tZ, aData, aRandom);
			setRandomBricks(aWorld, aChunkX+tX, aData.mOffsetY-2, aChunkZ+tZ, aData, aRandom);
		}
		
		for (int tY = aData.mOffsetY-3; tY >= 2 && temp; tY--) {
			temp = F;
			for (int tX = 6; tX <= 9 && !temp; tX++) for (int tZ = 6; tZ <= 9 && !temp; tZ++) {
				Block tBlock = aWorld.getBlock(aChunkX+tX, tY, aChunkZ+tZ);
				if (tBlock instanceof BlockFalling || !tBlock.isOpaqueCube()) temp = T;
			}
			if (temp) {
				for (int tX =  6; tX <=  9; tX++) for (int tZ =  6; tZ <=  9; tZ++) {
					setRandomBricks(aWorld, aChunkX+tX, tY  , aChunkZ+tZ, aData, aRandom);
				}
			} else {
				for (int tX =  5; tX <= 10; tX++) for (int tZ =  5; tZ <= 10; tZ++) {
					setSmoothBlock (aWorld, aChunkX+tX, tY+1, aChunkZ+tZ, aData, aRandom);
					setRandomBricks(aWorld, aChunkX+tX, tY  , aChunkZ+tZ, aData, aRandom);
					setRandomBricks(aWorld, aChunkX+tX, tY-1, aChunkZ+tZ, aData, aRandom);
					if (tY > 2 || aWorld.getBlock(aChunkX+tX, 0, aChunkZ+tZ).getBlockHardness(aWorld, aChunkX+tX, 0, aChunkZ+tZ) >= 0)
					setSmoothBlock (aWorld, aChunkX+tX, tY-2, aChunkZ+tZ, aData, aRandom);
				}
			}
		}
		
		return T;
	}
}
