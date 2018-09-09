package gregapi.tileentity.energy;

import static gregapi.data.CS.*;

import java.util.Collection;
import java.util.List;

import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.behavior.TE_Behavior_Active_Trinary;
import gregapi.tileentity.behavior.TE_Behavior_Energy_Capacitor;
import gregapi.tileentity.behavior.TE_Behavior_Energy_Converter;
import gregapi.tileentity.behavior.TE_Behavior_Energy_Stats;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Gregorius Techneticies
 */
public abstract class TileEntityBase10EnergyConverter extends TileEntityBase09FacingSingle implements ITileEntityEnergy, ITileEntityRunningActively {
	protected boolean mStopped = F;
	protected byte mExplosionPrevention = 0;
	
	public TE_Behavior_Energy_Stats mEnergyIN = null, mEnergyOUT = null;
	public TE_Behavior_Energy_Capacitor mStorage = null;
	public TE_Behavior_Energy_Converter mConverter = null;
	public TE_Behavior_Active_Trinary mActivity = null;
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		if (aNBT.hasKey(NBT_STOPPED)) mStopped = aNBT.getBoolean(NBT_STOPPED);
		mActivity = new TE_Behavior_Active_Trinary(this, aNBT);
		readEnergyBehavior(aNBT);
		readEnergyConverter(aNBT);
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
		mActivity.save(aNBT);
		writeEnergyBehavior(aNBT);
	}
	
	public void readEnergyBehavior(NBTTagCompound aNBT) {
		long tInput = aNBT.getLong(NBT_INPUT), tOutput = aNBT.getLong(NBT_OUTPUT);
		mStorage	= new TE_Behavior_Energy_Capacitor	(this, aNBT, tInput * 2);
		mEnergyIN	= new TE_Behavior_Energy_Stats		(this, aNBT, aNBT.hasKey(NBT_ENERGY_ACCEPTED) ? TagData.createTagData(aNBT.getString(NBT_ENERGY_ACCEPTED)) : TD.Energy.QU	, mStorage, tInput <= 16 ? 1 : tInput / 2, tInput, tInput * 2);
		mEnergyOUT	= new TE_Behavior_Energy_Stats		(this, aNBT, aNBT.hasKey(NBT_ENERGY_EMITTED ) ? TagData.createTagData(aNBT.getString(NBT_ENERGY_EMITTED )) : mEnergyIN.mType, mStorage, tOutput / 2, tOutput, tOutput * 2);
	}
	
	public void readEnergyConverter(NBTTagCompound aNBT) {
		mConverter	= new TE_Behavior_Energy_Converter	(this, aNBT, mStorage, mEnergyIN, mEnergyOUT, aNBT.hasKey(NBT_MULTIPLIER) ? aNBT.getLong(NBT_MULTIPLIER) : 1, aNBT.getBoolean(NBT_WASTE_ENERGY), F);
	}
	
	public void writeEnergyBehavior(NBTTagCompound aNBT) {
		mStorage.save(aNBT);
		mConverter.save(aNBT);
	}
	
	@Override
	public void addToolTips(List aList, ItemStack aStack, boolean aF3_H) {
		addToolTipsEnergy(aList, aStack, aF3_H);
		addToolTipsEfficiency(aList, aStack, aF3_H);
		super.addToolTips(aList, aStack, aF3_H);
	}
	
	public void addToolTipsEnergy(List aList, ItemStack aStack, boolean aF3_H) {
		mConverter.mEnergyIN .addToolTips(aList, aStack, aF3_H, getLocalisedInputSide (), F);
		mConverter.mEnergyOUT.addToolTips(aList, aStack, aF3_H, getLocalisedOutputSide(), T);
	}
	
	public void addToolTipsEfficiency(List aList, ItemStack aStack, boolean aF3_H) {
		LH.addToolTipsEfficiency(aList, aStack, aF3_H, mConverter);
	}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		if (aIsServerSide) {
			doConversion(aTimer);
			if (mTimer % 600 == 5) if (mActivity.mActive) doDefaultStructuralChecks(); else if (mExplosionPrevention > 0) mExplosionPrevention--;
		}
	}
	
	@Override public boolean onTickCheck(long aTimer) {return mActivity.check(mStopped) || super.onTickCheck(aTimer);}
	@Override public void setVisualData(byte aData) {mActivity.mState = aData;}
	@Override public byte getVisualData() {return mActivity.mState;}
	
	public void doConversion(long aTimer) {
		mActivity.mActive = mConverter.doConversion(aTimer, this, SIDE_ANY);
		if (mConverter.mOverloaded) {
			overload(mStorage.mEnergy, mConverter.mEnergyOUT.mType);
			mConverter.mOverloaded = F;
			mStorage.mEnergy = 0;
		}
	}
	
	@Override
	public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
		long tConsumed = mConverter.mEnergyIN.doInject(aSize, aAmount, aDoInject);
		if (mConverter.mEnergyIN.mOverloaded) {
			overload(aSize, aEnergyType);
			mConverter.mEnergyIN.mOverloaded = F;
		}
		return tConsumed;
	}
	
	public void overload(long aSize, TagData aEnergyType) {
		if (mExplosionPrevention < 100) {
			if (mTimer < 100) DEB.println("Machine overloaded on startup with: " + aSize + " " + aEnergyType.getLocalisedNameLong());
			mExplosionPrevention++;
			mStorage.mEnergy = 0;
		} else {
			overcharge(aSize, aEnergyType);
		}
	}
	
	@Override public boolean isEnergyType					(TagData aEnergyType, byte aSide, boolean aEmitting) {return (aEmitting?mConverter.mEnergyOUT:mConverter.mEnergyIN).isType(aEnergyType);}
	@Override public boolean isEnergyAcceptingFrom			(TagData aEnergyType, byte aSide, boolean aTheoretical) {return (aTheoretical || (!mStopped && (mConverter.mWasteEnergy || (mConverter.mEmitsEnergy == mConverter.mCanEmitEnergy)))) &&	(SIDES_INVALID[aSide] || isInput (aSide)) && super.isEnergyAcceptingFrom(aEnergyType, aSide, aTheoretical);}
	@Override public boolean isEnergyEmittingTo				(TagData aEnergyType, byte aSide, boolean aTheoretical) {return																															(SIDES_INVALID[aSide] || isOutput(aSide)) && super.isEnergyEmittingTo   (aEnergyType, aSide, aTheoretical);}
	@Override public long getEnergySizeOutputMin			(TagData aEnergyType, byte aSide) {return mConverter.mEnergyOUT.sizeMin(aEnergyType);}
	@Override public long getEnergySizeOutputRecommended	(TagData aEnergyType, byte aSide) {return mConverter.mEnergyOUT.sizeRec(aEnergyType);}
	@Override public long getEnergySizeOutputMax			(TagData aEnergyType, byte aSide) {return mConverter.mEnergyOUT.sizeMax(aEnergyType);}
	@Override public long getEnergySizeInputMin				(TagData aEnergyType, byte aSide) {return mConverter.mEnergyIN .sizeMin(aEnergyType);}
	@Override public long getEnergySizeInputRecommended		(TagData aEnergyType, byte aSide) {return mConverter.mEnergyIN .sizeRec(aEnergyType);}
	@Override public long getEnergySizeInputMax				(TagData aEnergyType, byte aSide) {return mConverter.mEnergyIN .sizeMax(aEnergyType);}
	@Override public Collection<TagData> getEnergyTypes(byte aSide) {return new ArrayListNoNulls(F, mConverter.mEnergyIN.mType, mConverter.mEnergyOUT.mType);}
	
	@Override public boolean canDrop(int aInventorySlot) {return F;}
	
	@Override public boolean getStateRunningPossible() {return T;}
	@Override public boolean getStateRunningPassively() {return mActivity.mActive;}
	@Override public boolean getStateRunningActively() {return mConverter.mEmitsEnergy;}
	public boolean setAdjacentOnOff(boolean aOnOff) {if (mConverter.mWasteEnergy) mStopped = !aOnOff; return !mStopped;}
	public boolean setStateOnOff(boolean aOnOff) {mStopped = !aOnOff; return !mStopped;}
	public boolean getStateOnOff() {return !mStopped;}
	
	// Stuff to Override
	
	public boolean isInput (byte aSide) {return aSide != mFacing;}
	public boolean isOutput(byte aSide) {return aSide == mFacing;}
	public String getLocalisedInputSide () {return LH.get(LH.FACE_ANYBUT_FRONT);}
	public String getLocalisedOutputSide() {return LH.get(LH.FACE_FRONT);}
}