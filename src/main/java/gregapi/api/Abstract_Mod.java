package gregapi.api;

import static gregapi.data.CS.*;

import java.util.List;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import gregapi.code.ArrayListNoNulls;
import gregapi.util.CR;
import gregapi.util.UT;

/**
 * @author Gregorius Techneticies
 * 
 * Base Class used for all my Mods.
 * Can also be used for GT-API based Mods such as Addons ;)
 */
public abstract class Abstract_Mod {
	public static final List<Abstract_Mod> MODS_USING_GT_API = new ArrayListNoNulls();
	
	/** Contains the amount of GT API Mods. Better than doing a constant List size check. */
	public static int sModCountUsingGTAPI = 0;
	
	/** Contains the amount of Phases the GT API Mods have already ran through. */
	public static int
	sStartedPreInit			= 0, sFinishedPreInit			= 0,
	sStartedInit			= 0, sFinishedInit				= 0,
	sStartedPostInit		= 0, sFinishedPostInit			= 0,
	sFinalized				= 0;
	
	/** List of all Configuration Files for Auto-Saving. */
	public static final List<Runnable> sConfigs = new ArrayListNoNulls();
	
	// ------------------------------ non-static stuff ------------------------------
	
	/** Contains the Proxy Class of this Mod. Can be null if there is no Proxy. */
	public Abstract_Proxy mProxy;
	
	/** Contains the Phases the Mod has already ran through. */
	public boolean
	mStartedPreInit			= F, mFinishedPreInit			= F,
	mStartedInit			= F, mFinishedInit				= F,
	mStartedPostInit		= F, mFinishedPostInit			= F,
	mFinalized				= F;
	
	/** Contains the amount of Server Start/Stop Phases the Mod has already ran through. */
	public int
	mStartedServerStarting	= 0, mFinishedServerStarting	= 0,
	mStartedServerStarted	= 0, mFinishedServerStarted		= 0,
	mStartedServerStopping	= 0, mFinishedServerStopping	= 0,
	mStartedServerStopped	= 0, mFinishedServerStopped		= 0;
	
	/** Event Lists where you can hook into the loading order of the Code, without having to care much about regular Mod load order. Note, that these Lists will be cleared and then set to null, right after they got executed once, in order to clean up some RAM. */
	public List<Runnable>
	mBeforePreInit			= new ArrayListNoNulls(), mAfterPreInit			= new ArrayListNoNulls(),
	mBeforeInit				= new ArrayListNoNulls(), mAfterInit			= new ArrayListNoNulls(),
	mBeforePostInit			= new ArrayListNoNulls(), mAfterPostInit		= new ArrayListNoNulls(),
	mFinalize				= new ArrayListNoNulls();
	
	/** Event Lists where you can hook into the loading order of the Code, without having to care much about regular Mod load order. */
	public final List<Runnable>
	mBeforeServerStarting	= new ArrayListNoNulls(), mAfterServerStarting	= new ArrayListNoNulls(),
	mBeforeServerStarted	= new ArrayListNoNulls(), mAfterServerStarted	= new ArrayListNoNulls(),
	mBeforeServerStopping	= new ArrayListNoNulls(), mAfterServerStopping	= new ArrayListNoNulls(),
	mBeforeServerStopped	= new ArrayListNoNulls(), mAfterServerStopped	= new ArrayListNoNulls();
	
	public Abstract_Mod() {
		sModCountUsingGTAPI++;
		MODS_USING_GT_API.add(this);
	}
	
	/** Return the Mod ID */
	public abstract String getModID();
	/** Return the Mod Name */
	public abstract String getModName();
	/** Used for logging purposes. */
	public abstract String getModNameForLog();
	/** Return the actual Proxy. Note: DO NOT RETURN mProxy! */
	public abstract Abstract_Proxy getProxy();
	/** Called on PreInit */
	public abstract void onModPreInit2(FMLPreInitializationEvent aEvent);
	/** Called on Init */
	public abstract void onModInit2(FMLInitializationEvent aEvent);
	/** Called on PostInit */
	public abstract void onModPostInit2(FMLPostInitializationEvent aEvent);
	/** Called on Server Start */
    public abstract void onModServerStarting2(FMLServerStartingEvent aEvent);
	/** Called after Server Start */
    public abstract void onModServerStarted2(FMLServerStartedEvent aEvent);
	/** Called on Server Stop */
    public abstract void onModServerStopping2(FMLServerStoppingEvent aEvent);
	/** Called after Server Stop */
    public abstract void onModServerStopped2(FMLServerStoppedEvent aEvent);
    
    
    
    @Override public String toString() {return getModID();}
    
    public void loadRunnables(String aName, List<Runnable> aList) {
		UT.LoadingBar.start(aName, aList.size());
    	for (Runnable tRunnable : aList) {
			String tString = tRunnable.toString();
			UT.LoadingBar.step(UT.Code.stringValid(tString)?tString:"UNNAMED");
    		try {tRunnable.run();} catch(Throwable e) {e.printStackTrace(ERR);}
    	}
    	UT.LoadingBar.finish();
    }
    
    public void loadRunnables(List<Runnable> aList) {
    	for (Runnable tRunnable : aList) try {tRunnable.run();} catch(Throwable e) {e.printStackTrace(ERR);}
    }
    
	// Just add Calls to these from within your Mods load phases.
	
	public void onModPreInit(FMLPreInitializationEvent aEvent) {
        if (mStartedPreInit) return;
		try {
			mProxy = getProxy();
	        OUT.println(getModNameForLog() + ": ======================");
	        ORD.println(getModNameForLog() + ": ======================");
	        
	        loadRunnables("Before PreInit", mBeforePreInit); mBeforePreInit.clear(); mBeforePreInit = null;
	    	
	        OUT.println(getModNameForLog() + ": PreInit-Phase started!");
	        ORD.println(getModNameForLog() + ": PreInit-Phase started!");
	    	
	        mStartedPreInit = T;
	        sStartedPreInit++;
	        if (mProxy != null) mProxy.onProxyBeforePreInit(this, aEvent);
	    	onModPreInit2(aEvent);
	    	if (mProxy != null) mProxy.onProxyAfterPreInit(this, aEvent);
	    	sFinishedPreInit++;
	    	mFinishedPreInit = T;
	    	
	        OUT.println(getModNameForLog() + ": PreInit-Phase finished!");
	        ORD.println(getModNameForLog() + ": PreInit-Phase finished!");
	        
	        loadRunnables("After PreInit", mAfterPreInit); mAfterPreInit.clear(); mAfterPreInit = null;
	        
	        loadRunnables("Saving Configs", sConfigs);
	    	
	        OUT.println(getModNameForLog() + ": =======================");
	        ORD.println(getModNameForLog() + ": =======================");
		} catch(Throwable e) {
	        loadRunnables("Saving Configs after Exception!", sConfigs);
	        e.printStackTrace(ERR);
			throw new RuntimeException(e);
		}
	}
	
	public void onModInit(FMLInitializationEvent aEvent) {
		if (mStartedInit) return;
		try {
	        OUT.println(getModNameForLog() + ": ===================");
	        ORD.println(getModNameForLog() + ": ===================");
	        
	        loadRunnables("Before Init", mBeforeInit); mBeforeInit.clear(); mBeforeInit = null;
	    	
	        OUT.println(getModNameForLog() + ": Init-Phase started!");
	        ORD.println(getModNameForLog() + ": Init-Phase started!");
	        
	        mStartedInit = T;
	        sStartedInit++;
	        if (mProxy != null) mProxy.onProxyBeforeInit(this, aEvent);
	        onModInit2(aEvent);
	        if (mProxy != null) mProxy.onProxyAfterInit(this, aEvent);
	        sFinishedInit++;
	        mFinishedInit = T;
	        
	        OUT.println(getModNameForLog() + ": Init-Phase finished!");
	        ORD.println(getModNameForLog() + ": Init-Phase finished!");
	        
	        loadRunnables("After Init", mAfterInit); mAfterInit.clear(); mAfterInit = null;
	        
	        loadRunnables("Saving Configs", sConfigs);
	        
	        OUT.println(getModNameForLog() + ": ====================");
	        ORD.println(getModNameForLog() + ": ====================");
		} catch(Throwable e) {
	        loadRunnables("Saving Configs after Exception!", sConfigs);
	        e.printStackTrace(ERR);
			throw new RuntimeException(e);
		}
	}
	
	public void onModPostInit(FMLPostInitializationEvent aEvent) {
		if (mStartedPostInit) return;
		try {
	        OUT.println(getModNameForLog() + ": =======================");
	        ORD.println(getModNameForLog() + ": =======================");
	        
	        loadRunnables("Before PostInit", mBeforePostInit); mBeforePostInit.clear(); mBeforePostInit = null;
	    	
	        OUT.println(getModNameForLog() + ": PostInit-Phase started!");
	        ORD.println(getModNameForLog() + ": PostInit-Phase started!");
	        
			mStartedPostInit = T;
			sStartedPostInit++;
			if (mProxy != null) mProxy.onProxyBeforePostInit(this, aEvent);
			onModPostInit2(aEvent);
			if (mProxy != null) mProxy.onProxyAfterPostInit(this, aEvent);
			sFinishedPostInit++;
			mFinishedPostInit = T;
			
	        OUT.println(getModNameForLog() + ": PostInit-Phase finished!");
	        ORD.println(getModNameForLog() + ": PostInit-Phase finished!");
	        
	        loadRunnables("After PostInit", mAfterPostInit); mAfterPostInit.clear(); mAfterPostInit = null;
	        
	        loadRunnables("Finalize", mFinalize); mFinalize.clear(); mFinalize = null;
	        
	        sFinalized++;
	        mFinalized = T;
	        
	        if (sFinalized >= sModCountUsingGTAPI) {
		        OUT.println(getModNameForLog() + ": Adding buffered Recipes.");
	        	CR.stopBufferingCraftingRecipes();
	        }
	        
	        loadRunnables("Saving Configs", sConfigs);
	    	
	        OUT.println(getModNameForLog() + ": ========================");
	        ORD.println(getModNameForLog() + ": ========================");
		} catch(Throwable e) {
	        loadRunnables("Saving Configs after Exception!", sConfigs);
	        e.printStackTrace(ERR);
			throw new RuntimeException(e);
		}
	}
	
    public void onModServerStarting(FMLServerStartingEvent aEvent) {
    	loadRunnables(mBeforeServerStarting);
    	mStartedServerStarting++;
    	if (mProxy != null) mProxy.onProxyBeforeServerStarting(this, aEvent);
    	onModServerStarting2(aEvent);
    	if (mProxy != null) mProxy.onProxyAfterServerStarting(this, aEvent);
    	mFinishedServerStarting++;
    	loadRunnables(mAfterServerStarting);
	}
    
    public void onModServerStarted(FMLServerStartedEvent aEvent) {
    	loadRunnables(mBeforeServerStarted);
		mStartedServerStarted++;
		if (mProxy != null) mProxy.onProxyBeforeServerStarted(this, aEvent);
		onModServerStarted2(aEvent);
		if (mProxy != null) mProxy.onProxyAfterServerStarted(this, aEvent);
		mFinishedServerStarted++;
		loadRunnables(mAfterServerStarted);
    }
    
    public void onModServerStopping(FMLServerStoppingEvent aEvent) {
    	loadRunnables(mBeforeServerStopping);
		mStartedServerStopping++;
		if (mProxy != null) mProxy.onProxyBeforeServerStopping(this, aEvent);
		onModServerStopping2(aEvent);
		if (mProxy != null) mProxy.onProxyAfterServerStopping(this, aEvent);
		mFinishedServerStopping++;
		loadRunnables(mAfterServerStopping);
    }
    
    public void onModServerStopped(FMLServerStoppedEvent aEvent) {
    	loadRunnables(mBeforeServerStopped);
		mStartedServerStopped++;
		if (mProxy != null) mProxy.onProxyBeforeServerStopped(this, aEvent);
		onModServerStopped2(aEvent);
		if (mProxy != null) mProxy.onProxyAfterServerStopped(this, aEvent);
		mFinishedServerStopped++;
		loadRunnables(mAfterServerStopped);
    }
}