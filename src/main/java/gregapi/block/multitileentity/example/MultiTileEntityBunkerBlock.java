package gregapi.block.multitileentity.example;

import static gregapi.data.CS.*;

import java.util.List;
import java.util.UUID;

import gregapi.block.multitileentity.IMultiTileEntity.IMTE_AddToolTips;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetPlayerRelativeBlockHardness;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnPlaced;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.notick.TileEntityBase05Paintable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author Gregorius Techneticies
 * 
 * An example implementation of a private Block.
 */
public class MultiTileEntityBunkerBlock extends TileEntityBase05Paintable implements IMTE_GetPlayerRelativeBlockHardness, IMTE_AddToolTips, IMTE_OnPlaced {
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_OWNER)) mOwner = UUID.fromString(aNBT.getString(NBT_OWNER));
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		if (mOwner != null) aNBT.setString(NBT_OWNER, mOwner.toString());
	}
	
	@Override
	public void addToolTips(List aList, ItemStack aStack, boolean aF3_H) {
		aList.add(Chat.ORANGE + LH.get(LH.OWNER_CONTROLLED));
	}
	
	@Override
	public boolean onPlaced(ItemStack aStack, EntityPlayer aPlayer, MultiTileEntityContainer aMTEContainer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (aPlayer != null) mOwner = aPlayer.getUniqueID();
		return T;
	}
	
	public static IIconContainer sColored = new Textures.BlockIcons.CustomIcon("bunker/block/colored"), sOverlay = new Textures.BlockIcons.CustomIcon("bunker/block/overlay");
	
	@Override public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureDefault.get(sColored, mRGBa), BlockTextureDefault.get(sOverlay)) : null;}
	@Override public String getTileEntityName() {return "gt.multitileentity.bunker.block";}
}