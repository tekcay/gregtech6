/**
 * Copyright (c) 2022 GregTech-6 Team
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

package gregtech.items.behaviors;

import com.mojang.authlib.GameProfile;
import gregapi.block.misc.BlockBaseBars;
import gregapi.block.misc.BlockBaseSpike;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.damage.DamageSources;
import gregapi.data.*;
import gregapi.enchants.Enchantment_EnderDamage;
import gregapi.item.multiitem.MultiItem;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.item.multiitem.behaviors.IBehavior.AbstractBehaviorDefault;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtech.tileentity.misc.MultiTileEntityGregOLantern;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.UUID;

import static gregapi.data.CS.*;

public class Behavior_Gun extends AbstractBehaviorDefault {
	public static Behavior_Gun BULLETS_SMALL  = new Behavior_Gun(TD.Projectiles.BULLET_SMALL , 10000, 12);
	public static Behavior_Gun BULLETS_MEDIUM = new Behavior_Gun(TD.Projectiles.BULLET_MEDIUM, 17500,  8);
	public static Behavior_Gun BULLETS_LARGE  = new Behavior_Gun(TD.Projectiles.BULLET_LARGE , 25000,  4);
	
	public final TagData mBulletType;
	public final long mPower;
	public final byte mAmmoPerMag;
	
	public Behavior_Gun(TagData aBulletType, long aPower, long aAmmoPerMag) {
		mBulletType = aBulletType;
		mPower = aPower;
		mAmmoPerMag = UT.Code.bindStack(aAmmoPerMag);
	}
	
	public boolean shoot(ItemStack aGun, ItemStack aBullet, EntityPlayer aPlayer) {
		// Making sure all Data is correct.
		aGun    = ST.update(aGun   , aPlayer);
		aBullet = ST.update(aBullet, aPlayer);
		// What's the Angle we are looking from and to?
		Vec3
		tDir = aPlayer.getLookVec(),
		tPos = Vec3.createVectorHelper(aPlayer.posX, aPlayer.posY + aPlayer.getEyeHeight(), aPlayer.posZ),
		tAim = tPos.addVector(tDir.xCoord * 100, tDir.yCoord * 100, tDir.zCoord * 100);
		// List all the Blocks that are on the way.
		List<ChunkCoordinates> aCoords = WD.ray(T, T, 201, tPos.xCoord, tPos.yCoord, tPos.zCoord, tAim.xCoord, tAim.yCoord, tAim.zCoord);
		// Gather random Information about the first Block.
		ChunkCoordinates oCoord = aCoords.get(0), aCoord = oCoord, nCoord = oCoord;
		Block oBlock = NB, aBlock = oBlock = WD.block(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
		byte  oMeta  =  0, aMeta  = oMeta  = WD.meta (aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
		// Are we shooting from under Water?
		boolean tWater = WD.liquid(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
		// Bullet related Stats
		int tFireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, aGun) + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, aBullet);
		
		
		
		// Make a List of all possible Targets.
		List tEntities = aPlayer.worldObj.getEntitiesWithinAABBExcludingEntity(aPlayer, AxisAlignedBB.getBoundingBox(Math.min(tPos.xCoord, tAim.xCoord), Math.min(tPos.yCoord, tAim.yCoord), Math.min(tPos.zCoord, tAim.zCoord), Math.max(tPos.xCoord, tAim.xCoord), Math.max(tPos.yCoord, tAim.yCoord), Math.max(tPos.zCoord, tAim.zCoord)));
		List<EntityLivingBase> tTargets = new ArrayListNoNulls<>();
		for (Object tEntity : tEntities) if (tEntity instanceof EntityLivingBase) {
			AxisAlignedBB tBox = ((EntityLivingBase)tEntity).boundingBox;
			if (tBox != null && tBox.calculateIntercept(tPos, tAim) != null) {tTargets.add((EntityLivingBase)tEntity); continue;}
		}
		// Actually do the shooting now!
		long tPower = mPower + 2000L*EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, aGun);
		for (int i = 1, ii = aCoords.size()-1; i < ii; i++) {
			
			if (tPower<=0) {
				// TODO Maybe drop the Round as an Item at ***oCoord***.
				if (tFireAspect > 2) WD.burn(aPlayer.worldObj, oCoord, T, T);
				return T;
			}
			
			oCoord = aCoords.get(i-1);
			aCoord = aCoords.get(i  );
			nCoord = aCoords.get(i+1);
			
			oBlock = aBlock;
			oMeta  = aMeta;
			
			aBlock = WD.block(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
			aMeta  = WD.meta (aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
			
			
			for (int j = 0; j < tTargets.size(); j++) {
				if (tTargets.get(j).getDistanceSq(aCoord.posX+0.5, aCoord.posY+0.5, aCoord.posZ+0.5) < tTargets.get(j).getDistanceSq(nCoord.posX+0.5, nCoord.posY+0.5, nCoord.posZ+0.5)) {
					if (hit(aGun, aBullet, aPlayer, tTargets.remove(j--), tPower, tDir)) {
						tPower-=10000;
						// If the bullet hits an Entity it should not possibly drop itself.
						if (tPower<=0) return T;
					}
				}
			}
			
			if (WD.liquid(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ)) {
				if (!tWater) {
					tWater = T;
					UT.Sounds.send(SFX.MC_LIQUID_SPLASH, aPlayer.worldObj, aCoord);
					// if high velocity break entirely, otherwise half the remaining power.
					if (tPower>10000) tPower=0; else tPower/=2;
				}
				continue;
			}
			
			tWater = F;
			
			if (aBlock instanceof BlockPumpkin || WD.te(aPlayer.worldObj, aCoord, T) instanceof MultiTileEntityGregOLantern) {
				if (RNGSUS.nextInt(3) == 0) {
					ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, ST.make(Blocks.pumpkin, 1, 0));
				} else {
					ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, ST.make(Items.pumpkin_seeds, 1+RNGSUS.nextInt(3), 0));
				}
				WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
				if (tFireAspect > 1) WD.fire(aPlayer.worldObj, aCoord, F);
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=3000;
				continue;
			}
			if (aBlock == Blocks.melon_block) {
				ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, ST.make(Items.melon      , 1+RNGSUS.nextInt(6), 0));
				ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, ST.make(Items.melon_seeds, 1+RNGSUS.nextInt(3), 0));
				WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
				if (tFireAspect > 1) WD.fire(aPlayer.worldObj, aCoord, F);
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=3000;
				continue;
			}
			if (aBlock == Blocks.cactus) {
				ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, ST.make(Blocks.cactus, 1, 0));
				WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
				if (tFireAspect > 1) WD.fire(aPlayer.worldObj, aCoord, F);
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=3000;
				continue;
			}
			if (aBlock == Blocks.cocoa) {
				ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, IL.Dye_Cocoa.get(1));
				WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
				if (tFireAspect > 1) WD.fire(aPlayer.worldObj, aCoord, F);
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=2000;
				continue;
			}
			if (aBlock == Blocks.wool || aBlock.getMaterial() == Material.carpet) {
				if (tFireAspect > 1) {
					WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
					WD.fire(aPlayer.worldObj, aCoord, F);
				}
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=4000;
				continue;
			}
			if (aBlock.getMaterial() == Material.glass || aBlock == Blocks.redstone_lamp || aBlock == Blocks.lit_redstone_lamp) {
				OreDictItemData tData = OM.anydata(ST.make(aBlock, 1, aMeta));
				for (OreDictMaterialStack tMaterial : tData.getAllMaterialStacks()) {
					long tAmount = tMaterial.mAmount / OP.scrapGt.mAmount;
					while (tAmount-->0) {
						ST.drop(aPlayer.worldObj, aCoord.posX+0.2+RNGSUS.nextFloat()*0.6, aCoord.posY+0.1+RNGSUS.nextFloat()*0.5, aCoord.posZ+0.2+RNGSUS.nextFloat()*0.6, OP.scrapGt.mat(tMaterial.mMaterial, 1));
					}
				}
				
				WD.set(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, NB, 0, 3);
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower-=2000;
				continue;
			}
			
			if (aBlock == Blocks.fence || aBlock == Blocks.fence_gate || aBlock == Blocks.web || aBlock == Blocks.mob_spawner || aBlock instanceof BlockPane || aBlock instanceof BlockRail || aBlock instanceof BlockTorch || aBlock instanceof BlockBaseBars || aBlock instanceof BlockBaseSpike || aBlock.getMaterial() == Material.cactus || aBlock.getMaterial() == Material.fire || aBlock.getMaterial() == Material.air || aBlock.getMaterial() == Material.carpet || aBlock.getMaterial() == Material.cloth || aBlock.getMaterial() == Material.leaves || aBlock.getMaterial() == Material.plants || aBlock.getMaterial() == Material.vine) {
				// Just ignore or assume the Player shot through them.
				tPower-=10;
				continue;
			}
			
			if (aBlock instanceof BlockStairs || WD.opq(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ, T, F)) {
				UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
				tPower=0;
				continue;
			}
			
			if (aBlock.canCollideCheck(aMeta, F) || aBlock.canCollideCheck(aMeta, T)) {
				AxisAlignedBB tBox = aBlock.getCollisionBoundingBoxFromPool(aPlayer.worldObj, aCoord.posX, aCoord.posY, aCoord.posZ);
				if (tBox != null && tBox.calculateIntercept(tPos, tAim) != null) {
					UT.Sounds.send(aBlock.stepSound.getBreakSound(), aPlayer.worldObj, aCoord);
					tPower=0;
					continue;
				}
			}
			
			tPower-=10;
		}
		return F;
	}
	
	public boolean hit(ItemStack aGun, ItemStack aBullet, EntityPlayer aPlayer, EntityLivingBase aTarget, long aPower, Vec3 aDir) {
		// Just pretend we miss the Target if it is in its Invulnerability Frames, this will end up hitting whatever is behind the Target instead.
		if (aTarget.hurtResistantTime > 0) return F;
		// Player specific immunities, and I guess friendly fire prevention too.
		if (aTarget instanceof EntityPlayer && (((EntityPlayer)aTarget).capabilities.disableDamage || !aPlayer.canAttackPlayer((EntityPlayer)aTarget))) return F;
		// Endermen require Disjunction Enchantment on the Bullet, or having a Weakness Potion Effect on them.
		if (aTarget instanceof EntityEnderman && aTarget.getActivePotionEffect(Potion.weakness) == null && EnchantmentHelper.getEnchantmentLevel(Enchantment_EnderDamage.INSTANCE.effectId, aBullet) <= 0) return F;
		
		OreDictItemData tData = OM.anydata(aBullet);
		OreDictMaterial tGunMat = MultiItemTool.getPrimaryMaterial(aGun, MT.Steel);
		
		float
		tMagicDamage = EnchantmentHelper.func_152377_a(aBullet, aTarget.getCreatureAttribute()),
		tDamage = Math.min(2.0F, aPower/5000.0F) * (Math.max(0, tGunMat.mToolQuality*0.5F + (tData!=null&&tData.hasValidMaterialData()?(float)tData.mMaterial.weight() / 50.0F : 1)));
		int
		tFireDamage = 4 * (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, aGun) + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, aBullet)),
		tKnockback  =     (EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, aGun) + EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback .effectId, aBullet));
		
		if (tFireDamage > 0) aTarget.setFire(tFireDamage);
		
		EntityPlayer tPlayer = aPlayer;
		if (!(aTarget instanceof EntityPlayer) && aPlayer.worldObj instanceof WorldServer) {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, aBullet) > 0) {
				tPlayer = FakePlayerFactory.get((WorldServer)aPlayer.worldObj, new GameProfile(new UUID(0, 0), ((EntityLivingBase)aPlayer).getCommandSenderName()));
				tPlayer.inventory.currentItem = 0;
				tPlayer.inventory.setInventorySlotContents(0, aBullet);
				tPlayer.setPositionAndRotation(aPlayer.posX, aPlayer.posY, aPlayer.posZ, aPlayer.rotationYaw, aPlayer.rotationPitch);
				tPlayer.setDead();
			}
		}
		DamageSource tDamageSource = DamageSources.getCombatDamage("player", tPlayer, DamageSources.getDeathMessage(aPlayer, aTarget, "[VICTIM] got shot by [KILLER] with a Gun")).setProjectile();
		if (aTarget.attackEntityFrom(tDamageSource, (tDamage + tMagicDamage) * TFC_DAMAGE_MULTIPLIER)) {
			aTarget.hurtResistantTime = aTarget.maxHurtResistantTime;
			if (aTarget instanceof EntityCreeper && tFireDamage > 0) ((EntityCreeper)aTarget).func_146079_cb();
			if (tKnockback > 0) aTarget.addVelocity(aDir.xCoord * tKnockback * aPower / 50000.0, 0.05, aDir.zCoord * tKnockback * aPower / 50000.0);
			UT.Enchantments.applyBullshitA(aTarget, aPlayer, aBullet);
			UT.Enchantments.applyBullshitB(aPlayer, aTarget, aBullet);
			if (aTarget instanceof EntityPlayer && aPlayer instanceof EntityPlayerMP) ((EntityPlayerMP)aPlayer).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
			if (tMagicDamage > 0.0F) aPlayer.onEnchantmentCritical(aTarget);
		}
		return T;
	}
	
	@Override public boolean onRightClickEntity(MultiItem aItem, ItemStack aStack, EntityPlayer aPlayer, Entity aEntity) {onItemRightClick(aItem, aStack, aPlayer.worldObj, aPlayer); return T;}
	@Override public boolean onItemUse         (MultiItem aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, byte aSide, float hitX, float hitY, float hitZ) {onItemRightClick(aItem, aStack, aPlayer.worldObj, aPlayer); return T;}
	@Override public boolean onItemUseFirst    (MultiItem aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, byte aSide, float hitX, float hitY, float hitZ) {if (aWorld.isRemote) return F; onItemRightClick(aItem, aStack, aPlayer.worldObj, aPlayer); return T;}
	
	@Override
	public ItemStack onItemRightClick(MultiItem aItem, ItemStack aGun, World aWorld, EntityPlayer aPlayer) {
		if (aPlayer instanceof EntityPlayerMP) {
			if (aPlayer.isSneaking()) {
				// TODO: Reload/Unload Gun
			} else {
				if (!UT.Entities.hasInfiniteItems(aPlayer) && RNGSUS.nextInt(1+EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, aGun)) == 0) {
					// TODO Use up one Bullet!
				}
				// TODO: Select Bullet!
				shoot(aGun, OP.bulletGtSmall.mat(MT.Au, 1), aPlayer);
				UT.Sounds.send(SFX.MC_FIREWORK_BLAST_FAR, 128, 1.0F, aPlayer);
			}
		}
		return aGun;
	}
	
	@Override
	public List<String> getAdditionalToolTips(MultiItem aItem, List<String> aList, ItemStack aStack) {
		aList.add(LH.Chat.CYAN + LH.get(LH.WEAPON_SNEAK_RIGHTCLICK_TO_RELOAD));
		aList.add(LH.Chat.CYAN + LH.get(LH.WEAPON_HEAVIER_BULLETS_STRONGER));
		return aList;
	}
}
