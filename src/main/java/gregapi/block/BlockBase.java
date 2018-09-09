package gregapi.block;

import static gregapi.data.CS.*;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.data.LH;
import gregapi.data.OP;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Gregorius Techneticies
 */
public abstract class BlockBase extends Block implements IBlockBase {
	public final String mNameInternal;
	
	public BlockBase(Class<? extends ItemBlock> aItemClass, String aNameInternal, Material aMaterial, SoundType aSoundType) {
		super(aMaterial);
		setStepSound(aSoundType);
		setBlockName(mNameInternal = aNameInternal);
		GameRegistry.registerBlock(this, aItemClass == null ? ItemBlockBase.class : aItemClass, getUnlocalizedName());
		if (COMPAT_IC2 != null) COMPAT_IC2.addToExplosionWhitelist(this);
		LH.add(mNameInternal+"."+W+".name", "Any Sub-Block of this one");
	}
	
	@Override public final String getUnlocalizedName() {return mNameInternal;}
	@Override public String getLocalizedName() {return StatCollector.translateToLocal(mNameInternal+ ".name");}
	@Override public String getHarvestTool(int aMeta) {return TOOL_pickaxe;}
	@Override public int getHarvestLevel(int aMeta) {return 0;}
	@Override public boolean canSilkHarvest() {return T;}
	@Override public boolean isToolEffective(String aType, int aMeta) {return getHarvestTool(aMeta).equals(aType);}
	@Override public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {return F;}
	@Override public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ)  {return T;}
	@Override public boolean renderAsNormalBlock() {return T;}
	@Override public boolean isOpaqueCube() {return T;}
	@Override public boolean func_149730_j() {return isOpaqueCube();}
	@Override public boolean isSideSolid(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aDirection) {return isSideSolid(aWorld.getBlockMetadata(aX, aY, aZ), UT.Code.side(aDirection));}
	@Override public int damageDropped(int aMeta) {return aMeta;}
	@Override public int quantityDropped(Random par1Random) {return 1;}
	@Override public ItemStack createStackedBlock(int aMeta) {return ST.make(this, 1, damageDropped(aMeta));}
	@Override public int getDamageValue(World aWorld, int aX, int aY, int aZ) {return aWorld.getBlockMetadata(aX, aY, aZ);}
	@Override public int getLightOpacity() {return LIGHT_OPACITY_MAX;}
	@Override public Item getItemDropped(int aMeta, Random aRandom, int aFortune) {return Item.getItemFromBlock(this);}
	@Override public Item getItem(World aWorld, int aX, int aY, int aZ) {return Item.getItemFromBlock(this);}
	@Override public void registerBlockIcons(IIconRegister aIconRegister) {/**/}
	@Override public boolean canSustainPlant(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide, IPlantable aPlant) {return F;}
	@Override public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess aWorld, int aX, int aY, int aZ) {int aMeta = aWorld.getBlockMetadata(aX, aY, aZ); return canCreatureSpawn(aMeta) && isSideSolid(aMeta, SIDE_TOP);}
	@Override public boolean isFireSource(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {return F;}
	@Override public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {return F;}
	@Override public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {return 0;}
	@Override public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {return 0;}
	@Override public Material getMaterial() {return GAPI_POST.mFinishedServerStarted > 0 && blockMaterial.getCanBurn() && !isFlammable(null, 0, 0, 0, FORGE_DIR[SIDE_ANY]) && BlockStaticLiquid.class.getName().equalsIgnoreCase(new Throwable().getStackTrace()[2].getClassName()) ? Material.iron : blockMaterial;}
	@Override public float getExplosionResistance(Entity aEntity, World aWorld, int aX, int aY, int aZ, double eX, double eY, double eZ) {return getExplosionResistance(aWorld.getBlockMetadata(aX, aY, aZ));}
	@Override public float getExplosionResistance(Entity aEntity) {return getExplosionResistance(0);}
	@Override public Block getBlock() {return this;}
	@Override public final void onNeighborBlockChange(World aWorld, int aX, int aY, int aZ, Block aBlock) {if (useGravity(WD.meta(aWorld, aX, aY, aZ))) aWorld.scheduleBlockUpdate(aX, aY, aZ, this, 2); onNeighborBlockChange2(aWorld, aX, aY, aZ, aBlock);}
	@Override public final void onBlockAdded(World aWorld, int aX, int aY, int aZ) {if (useGravity(WD.meta(aWorld, aX, aY, aZ))) aWorld.scheduleBlockUpdate(aX, aY, aZ, this, 2); onBlockAdded2(aWorld, aX, aY, aZ);}
	@Override public IIcon getIcon(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {return getIcon(aSide, aWorld.getBlockMetadata(aX, aY, aZ));}
	
	@Override public String name(int aMeta) {return aMeta == W ? mNameInternal : mNameInternal + "." + aMeta;}
	@Override public boolean useGravity(int aMeta) {return F;}
	@Override public boolean doesWalkSpeed(short aMeta) {return F;}
	@Override public boolean canCreatureSpawn(int aMeta) {return F;}
	@Override public boolean isSealable(int aMeta, byte aSide) {return isSideSolid(aMeta, aSide);}
	@Override public void addInformation(ItemStack aStack, int aMeta, EntityPlayer aPlayer, List aList, boolean aF3_H) {/**/}
	@Override public float getExplosionResistance(int aMeta) {return 10.0F;}
	@Override public int getItemStackLimit(ItemStack aStack) {return UT.Code.bindStack(OP.block.mDefaultStackSize);}
	@Override public ItemStack onItemRightClick(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {return aStack;}
	
	public boolean isSideSolid(int aMeta, byte aSide) {return T;}
	public void updateTick2(World aWorld, int aX, int aY, int aZ, Random aRandom) {/**/}
	public void onNeighborBlockChange2(World aWorld, int aX, int aY, int aZ, Block aBlock) {/**/}
	public void onBlockAdded2(World aWorld, int aX, int aY, int aZ) {/**/}
	
	@Override
	public final void updateTick(World aWorld, int aX, int aY, int aZ, Random aRandom) {
		if (aWorld.isRemote || checkGravity(aWorld, aX, aY, aZ)) return;
		updateTick2(aWorld, aX, aY, aZ, aRandom);
	}
	
	public boolean checkGravity(World aWorld, int aX, int aY, int aZ) {
		short aMeta = WD.meta(aWorld, aX, aY, aZ);
		if (aY > 0 && useGravity(aMeta) && BlockFalling.func_149831_e(aWorld, aX, aY - 1, aZ)) {
			if (!BlockFalling.fallInstantly && aWorld.checkChunksExist(aX-32, aY-32, aZ-32, aX+32, aY+32, aZ+32)) {
				if (!aWorld.isRemote) aWorld.spawnEntityInWorld(new EntityFallingBlock(aWorld, aX+0.5, aY+0.5, aZ+0.5, this, aMeta));
			} else {
				aWorld.setBlockToAir(aX, aY, aZ);
				while (BlockFalling.func_149831_e(aWorld, aX, aY-1, aZ) && aY > 0) --aY;
				if (aY > 0) WD.set(aWorld, aX, aY, aZ, this, aMeta, 2);
			}
			return T;
		}
		return F;
	}
}