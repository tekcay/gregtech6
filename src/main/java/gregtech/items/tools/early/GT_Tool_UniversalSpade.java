package gregtech.items.tools.early;

import static gregapi.data.CS.*;

import gregapi.data.CS.SFX;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.item.multiitem.behaviors.Behavior_Place_Paddy;
import gregapi.item.multiitem.behaviors.Behavior_Place_Path;
import gregapi.item.multiitem.behaviors.Behavior_Place_Torch;
import gregapi.item.multiitem.behaviors.Behavior_Tool;
import gregapi.item.multiitem.tools.ToolStats;
import gregapi.render.IIconContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class GT_Tool_UniversalSpade extends ToolStats {
	@Override
	public int getToolDamagePerBlockBreak() {
		return 100;
	}
	
	@Override
	public int getToolDamagePerDropConversion() {
		return 100;
	}
	
	@Override
	public int getToolDamagePerContainerCraft() {
		return 400;
	}
	
	@Override
	public int getToolDamagePerEntityAttack() {
		return 100;
	}
	
	@Override
	public int getBaseQuality() {
		return 0;
	}
	
	@Override
	public float getBaseDamage() {
		return 3.0F;
	}
	
	@Override
	public float getSpeedMultiplier() {
		return 0.75F;
	}
	
	@Override
	public float getMaxDurabilityMultiplier() {
		return 1.0F;
	}
	
	@Override
	public boolean canBlock() {
		return T;
	}
	
	@Override
	public boolean isCrowbar() {
		return T;
	}
	
	@Override
	public boolean isWeapon() {
		return T;
	}
	
	@Override
	public boolean isMinableBlock(Block aBlock, byte aMetaData) {
		String tTool = aBlock.getHarvestTool(aMetaData);
		return (tTool != null && (tTool.equalsIgnoreCase(TOOL_shovel) || tTool.equalsIgnoreCase(TOOL_axe) || tTool.equalsIgnoreCase(TOOL_saw) || tTool.equalsIgnoreCase(TOOL_sword) || tTool.equalsIgnoreCase(TOOL_crowbar))) || aBlock.getMaterial() == Material.sand || aBlock.getMaterial() == Material.grass || aBlock.getMaterial() == Material.ground || aBlock.getMaterial() == Material.snow || aBlock.getMaterial() == Material.craftedSnow || aBlock.getMaterial() == Material.clay  || aBlock.getMaterial() == Material.leaves || aBlock.getMaterial() == Material.vine || aBlock.getMaterial() == Material.wood || aBlock.getMaterial() == Material.cactus || aBlock.getMaterial() == Material.circuits || aBlock.getMaterial() == Material.gourd || aBlock.getMaterial() == Material.web || aBlock.getMaterial() == Material.cloth || aBlock.getMaterial() == Material.carpet || aBlock.getMaterial() == Material.plants || aBlock.getMaterial() == Material.cake || aBlock.getMaterial() == Material.tnt || aBlock.getMaterial() == Material.sponge;
	}
	
	@Override
	public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
		return aIsToolHead ? MultiItemTool.getPrimaryMaterial(aStack, MT.Steel).mTextureSetsItems.get(OP.toolHeadUniversalSpade.mIconIndexItem) : MultiItemTool.getSecondaryMaterial(aStack, MT.Steel).mTextureSetsItems.get(OP.stick.mIconIndexItem);
	}
	
	@Override
	public short[] getRGBa(boolean aIsToolHead, ItemStack aStack) {
		return aIsToolHead ? MultiItemTool.getPrimaryMaterial(aStack, MT.Steel).mRGBaSolid : MultiItemTool.getSecondaryMaterial(aStack, MT.Steel).mRGBaSolid;
	}
	
	@Override
	public void onStatsAddedToTool(MultiItemTool aItem, int aID) {
		aItem.addItemBehavior(aID, new Behavior_Place_Path(50));
		aItem.addItemBehavior(aID, new Behavior_Place_Paddy(50));
		aItem.addItemBehavior(aID, new Behavior_Tool(TOOL_crowbar, SFX.MC_BREAK, 200, F));
		aItem.addItemBehavior(aID, new Behavior_Tool(TOOL_shovel, SFX.MC_DIG_GRAVEL, 100, F));
		aItem.addItemBehavior(aID, Behavior_Place_Torch.INSTANCE);
	}
	
	@Override
	public void onToolCrafted(ItemStack aStack, EntityPlayer aPlayer) {
		super.onToolCrafted(aStack, aPlayer);
		aPlayer.triggerAchievement(AchievementList.buildSword);
	}
	
	@Override
	public String getDeathMessage() {
		return "[VICTIM] has been digged by [KILLER]";
	}
}