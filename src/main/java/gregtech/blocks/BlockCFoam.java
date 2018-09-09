package gregtech.blocks;

import static gregapi.data.CS.*;

import gregapi.block.IBlockFoamable;
import gregapi.block.metatype.BlockColored;
import gregapi.block.metatype.BlockMetaType;
import gregapi.block.metatype.ItemBlockMetaType;
import gregapi.data.MT;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.IIconContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

public class BlockCFoam extends BlockColored implements IBlockFoamable {
	public BlockCFoam(String aUnlocalised) {
		super(ItemBlockMetaType.class, Material.rock, soundTypeStone, aUnlocalised, "C-Foam", MT.ConstructionFoam, 4.0F, 1.5F, 1, Textures.BlockIcons.CFOAMS);
	}
	
	@Override
	protected BlockMetaType makeSlab(Class<? extends ItemBlock> aItemClass, Material aVanillaMaterial, SoundType aVanillaSoundType, String aName, String aDefaultLocalised, OreDictMaterial aMaterial, float aResistanceMultiplier, float aHardnessMultiplier, int aHarvestLevel, int aCount, IIconContainer[] aIcons, byte aSlabType, BlockMetaType aBlock) {
		return new BlockCFoam(aItemClass, aVanillaMaterial, aVanillaSoundType, aName, aDefaultLocalised, aMaterial, aResistanceMultiplier, aHardnessMultiplier, aHarvestLevel, aCount, aIcons, aSlabType, aBlock);
	}
	
	protected BlockCFoam(Class<? extends ItemBlock> aItemClass, Material aVanillaMaterial, SoundType aVanillaSoundType, String aName, String aDefaultLocalised, OreDictMaterial aMaterial, float aResistanceMultiplier, float aHardnessMultiplier, int aHarvestLevel, int aCount, IIconContainer[] aIcons, byte aSlabType, BlockMetaType aBlock) {
		super(aItemClass, aVanillaMaterial, aVanillaSoundType, aName, aDefaultLocalised, aMaterial, aResistanceMultiplier, aHardnessMultiplier, aHarvestLevel, aCount, aIcons, aSlabType, aBlock);
	}
	
	@Override
	public boolean applyFoam(World aWorld, int aX, int aY, int aZ, byte aSide, short[] aCFoamRGB, byte aVanillaColor) {
		return F;
	}
	
	@Override
	public boolean dryFoam(World aWorld, int aX, int aY, int aZ, byte aSide) {
		return F;
	}
	
	@Override
	public boolean removeFoam(World aWorld, int aX, int aY, int aZ, byte aSide) {
		return aWorld.setBlock(aX, aY, aZ, NB, 0, 3);
	}
	
	@Override
	public boolean hasFoam(World aWorld, int aX, int aY, int aZ, byte aSide) {
		return T;
	}
	
	@Override
	public boolean driedFoam(World aWorld, int aX, int aY, int aZ, byte aSide) {
		return T;
	}
}