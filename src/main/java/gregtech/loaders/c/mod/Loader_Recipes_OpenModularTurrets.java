package gregtech.loaders.c.mod;

import static gregapi.data.CS.*;

import gregapi.config.ConfigCategories;
import gregapi.data.ANY;
import gregapi.data.CS.BlocksGT;
import gregapi.data.CS.ConfigsGT;
import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.data.RM;
import gregapi.oredict.IOreDictListenerEvent;
import gregapi.oredict.OreDictListenerEvent_Names;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;

public class Loader_Recipes_OpenModularTurrets implements Runnable {
	@Override
	public void run() {if (MD.OMT.mLoaded) {OUT.println("GT_Mod: Doing Open Modular Turrets Recipes.");
		CR.remout(MD.OMT
		, "fenceTierOne", "fenceTierTwo", "fenceTierThree", "fenceTierFour", "fenceTierFive"
		, "hardWallTierOne", "hardWallTierTwo", "hardWallTierThree", "hardWallTierFour", "hardWallTierFive"
		, "baseTierWood", "baseTierOneBlock", "baseTierTwoBlock", "baseTierThreeBlock", "baseTierFourBlock"
		, "bulletCraftable", "grenadeCraftable", "rocketCraftable", "ferroSlug", "blazingClayCraftable"
		);
		
		CR.shaped	(ST.make(MD.OMT, "fenceTierOne"				, 8, 0), CR.DEF_REV_NCC	, " x ", "WSW", 'W', OP.wireFine.dat(ANY.Iron), 'S', OP.stick.dat(ANY.Iron));
		CR.shaped	(ST.make(MD.OMT, "fenceTierTwo"				, 1, 0), CR.DEF_REV_NCC	, " f ", "BFB", 'B', OP.bolt.dat(MT.TinAlloy		), 'F', ST.make(MD.OMT, "fenceTierOne", 1, 0));
		CR.shaped	(ST.make(MD.OMT, "fenceTierThree"			, 1, 0), CR.DEF_REV_NCC	, " f ", "BFB", 'B', OP.bolt.dat(ANY.Steel			), 'F', ST.make(MD.OMT, "fenceTierOne", 1, 0));
		CR.shaped	(ST.make(MD.OMT, "fenceTierFour"			, 1, 0), CR.DEF_REV_NCC	, " f ", "BFB", 'B', OP.bolt.dat(MT.Ti				), 'F', ST.make(MD.OMT, "fenceTierOne", 1, 0));
		CR.shaped	(ST.make(MD.OMT, "fenceTierFive"			, 1, 0), CR.DEF_REV_NCC	, " f ", "BFB", 'B', OP.bolt.dat(MT.TungstenSteel	), 'F', ST.make(MD.OMT, "fenceTierOne", 1, 0));
		
		RM.generify	(ST.make(MD.OMT, "hardWallTierOne"			, 1, W), ST.make(BlocksGT.Concrete				, 1, 7));
		RM.generify	(ST.make(MD.OMT, "hardWallTierTwo"			, 1, W), ST.make(BlocksGT.Concrete				, 1, 7));
		RM.generify	(ST.make(MD.OMT, "hardWallTierThree"		, 1, W), ST.make(BlocksGT.Concrete				, 1, 7));
		RM.generify	(ST.make(MD.OMT, "hardWallTierFour"			, 1, W), ST.make(BlocksGT.ConcreteReinforced	, 1, 7));
		RM.generify	(ST.make(MD.OMT, "hardWallTierFive"			, 1, W), ST.make(BlocksGT.ConcreteReinforced	, 1, 7));
		
		CR.shapeless(ST.make(BlocksGT.Concrete					, 1, 7), CR.DEF_NCC		, new Object[] {ST.make(MD.OMT, "hardWallTierOne"	, 1, W)});
		CR.shapeless(ST.make(BlocksGT.Concrete					, 1, 7), CR.DEF_NCC		, new Object[] {ST.make(MD.OMT, "hardWallTierTwo"	, 1, W)});
		CR.shapeless(ST.make(BlocksGT.Concrete					, 1, 7), CR.DEF_NCC		, new Object[] {ST.make(MD.OMT, "hardWallTierThree"	, 1, W)});
		CR.shapeless(ST.make(BlocksGT.ConcreteReinforced		, 1, 7), CR.DEF_NCC		, new Object[] {ST.make(MD.OMT, "hardWallTierFour"	, 1, W)});
		CR.shapeless(ST.make(BlocksGT.ConcreteReinforced		, 1, 7), CR.DEF_NCC		, new Object[] {ST.make(MD.OMT, "hardWallTierFive"	, 1, W)});
		
		OM.data				(MD.OMT, "hardWallTierOne"			, 1, 0, MT.Concrete, U);
		CR.shapeless(ST.make(MD.OMT, "hardWallTierOne"			, 1, 0), CR.DEF_NCC		, new Object[] {ST.make(BlocksGT.Concrete, 1, W)});
		CR.shaped	(ST.make(MD.OMT, "hardWallTierTwo"			, 1, 0), CR.DEF_REV_NCC	, "Be" , " W" , 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(BlocksGT.Concrete				, 1, W));
		CR.shaped	(ST.make(MD.OMT, "hardWallTierTwo"			, 1, 0), CR.DEF_NCC		, "Be" , " W" , 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(MD.OMT, "hardWallTierOne"		, 1, 0));
		CR.shaped	(ST.make(MD.OMT, "hardWallTierThree"		, 1, 0), CR.DEF_REV_NCC	, "Be" , " W" , 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(MD.OMT, "hardWallTierTwo"		, 1, 0));
		CR.shapeless(ST.make(MD.OMT, "hardWallTierFour"			, 1, 0), CR.DEF_NCC		, new Object[] {ST.make(BlocksGT.ConcreteReinforced, 1, W)});
		CR.shaped	(ST.make(MD.OMT, "hardWallTierFour"			, 1, 0), CR.DEF_REV_NCC	, "Be" , "BW" , 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(MD.OMT, "hardWallTierThree"	, 1, 0));
		CR.shaped	(ST.make(MD.OMT, "hardWallTierFive"			, 1, 0), CR.DEF_REV_NCC	, "BeB", "BWB", 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(BlocksGT.ConcreteReinforced	, 1, W));
		CR.shaped	(ST.make(MD.OMT, "hardWallTierFive"			, 1, 0), CR.DEF_NCC		, "BeB", "BWB", 'B', OP.bolt.dat(ANY.Iron), 'W', ST.make(MD.OMT, "hardWallTierFour"		, 1, 0));
		
		if (ConfigsGT.RECIPES.get(ConfigCategories.Recipes.harderrecipes, "open_modular_turrets", T)) {
		CR.shaped	(ST.make(MD.OMT, "baseTierWood"				, 1, 0), CR.DEF_REV_NCC	, "wMr", "TWT", "xXd", 'E', IL.EMITTERS[0], 'R', IL.SENSORS[0], 'M', IL.MOTORS[0], 'W', MT.DATA.CABLES_04[0], 'T', OP.screw.dat(ANY.Iron), 'X', OD.craftingChest);
		CR.shaped	(ST.make(MD.OMT, "baseTierOneBlock"			, 1, 0), CR.DEF_REV_NCC	, "CMC", "EBR", "WXW", 'E', IL.EMITTERS[1], 'R', IL.SENSORS[1], 'M', IL.MOTORS[1], 'W', MT.DATA.CABLES_01[1], 'C', OD_CIRCUITS[0], 'B', "gt:re-battery1", 'X', OP.casingMachine.dat(MT.TinAlloy));
		CR.shaped	(ST.make(MD.OMT, "baseTierTwoBlock"			, 1, 0), CR.DEF_REV_NCC	, "CMC", "EBR", "WXW", 'E', IL.EMITTERS[2], 'R', IL.SENSORS[2], 'M', IL.MOTORS[2], 'W', MT.DATA.CABLES_01[2], 'C', OD_CIRCUITS[2], 'B', "gt:re-battery2", 'X', OP.casingMachine.dat(ANY.Steel));
		CR.shaped	(ST.make(MD.OMT, "baseTierThreeBlock"		, 1, 0), CR.DEF_REV_NCC	, "CMC", "EBR", "WXW", 'E', IL.EMITTERS[3], 'R', IL.SENSORS[3], 'M', IL.MOTORS[3], 'W', MT.DATA.CABLES_01[3], 'C', OD_CIRCUITS[4], 'B', "gt:re-battery3", 'X', OP.casingMachine.dat(MT.Ti));
		CR.shaped	(ST.make(MD.OMT, "baseTierFourBlock"		, 1, 0), CR.DEF_REV_NCC	, "CMC", "EBR", "WXW", 'E', IL.EMITTERS[4], 'R', IL.SENSORS[4], 'M', IL.MOTORS[4], 'W', MT.DATA.CABLES_01[4], 'C', OD_CIRCUITS[6], 'B', "gt:re-battery3", 'X', OP.casingMachine.dat(MT.TungstenSteel));
		}
		
		CR.shaped	(ST.make(MD.OMT, "bulletCraftable"			, 8, 0), CR.DEF_NCC		, " X", 'X', OP.bulletGtSmall.dat(MT.Pb));
		CR.shaped	(ST.make(MD.OMT, "bulletCraftable"			, 8, 0), CR.DEF_NCC		, " X", 'X', OP.bulletGtSmall.dat(ANY.Steel));
		CR.shaped	(ST.make(MD.OMT, "grenadeCraftable"			,16, 0), CR.DEF_REV_NCC	, "BRB", "GPG", "BRB", 'P', OP.pipeTiny.dat(ANY.Iron), 'R', OP.ring.dat(ANY.Iron), 'B', OP.bolt.dat(ANY.Iron), 'G', OP.dustSmall.dat(MT.Gunpowder));
		CR.shaped	(ST.make(MD.OMT, "rocketCraftable"			, 8, 0), CR.DEF_REV_NCC	, " X ", " G ", "XPX", 'P', OP.pipeSmall.dat(ANY.Steel), 'X', OP.plateTiny.dat(ANY.Steel), 'G', OP.dust.dat(MT.Gunpowder));
		CR.shaped	(ST.make(MD.OMT, "ferroSlug"				,16, 0), CR.DEF_REV_NCC	, "BRB", "BRB", 'B', OP.bolt.dat(MT.SteelMagnetic), 'R', OP.ring.dat(MT.SteelMagnetic));
		
		OM.data(MD.OMT, "blazingClayCraftable", 32, W, ANY.Clay, U*4, MT.Redstone, U*4, MT.Blaze, U);
		
		for (OreDictMaterial tMat : ANY.Clay.mToThis) {
		RM.Mixer.addRecipeX(T, 16, 512, new ItemStack[] {OP.dust.mat(tMat, 4), OP.dust.mat(MT.Redstone, 4), OP.dust		.mat(MT.Blaze, 1)}, ST.make(MD.OMT, "blazingClayCraftable", 32, 0));
		RM.Mixer.addRecipeX(T, 16, 128, new ItemStack[] {OP.dust.mat(tMat, 1), OP.dust.mat(MT.Redstone, 1), OP.dustSmall.mat(MT.Blaze, 1)}, ST.make(MD.OMT, "blazingClayCraftable",  8, 0));
		}
		
		new OreDictListenerEvent_Names() {@Override public void addAllListeners() {
		addListener("itemClay", new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			RM.Mixer.addRecipeX(T, 16, 512, new ItemStack[] {ST.amount(4, aEvent.mStack), OP.dust.mat(MT.Redstone, 4), OP.dust		.mat(MT.Blaze, 1)}, ST.make(MD.OMT, "blazingClayCraftable", 32, 0));
			RM.Mixer.addRecipeX(T, 16, 128, new ItemStack[] {aEvent.mStack				, OP.dust.mat(MT.Redstone, 1), OP.dustSmall	.mat(MT.Blaze, 1)}, ST.make(MD.OMT, "blazingClayCraftable",  8, 0));
		}});
		addListener("blockClay", new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			RM.Mixer.addRecipeX(T, 16, 512, new ItemStack[] {aEvent.mStack				, OP.dust.mat(MT.Redstone, 4), OP.dust		.mat(MT.Blaze, 1)}, ST.make(MD.OMT, "blazingClayCraftable", 32, 0));
			RM.Mixer.addRecipeX(T, 16, 512, new ItemStack[] {aEvent.mStack				, OP.dust.mat(MT.Redstone, 4), OP.dustSmall	.mat(MT.Blaze, 4)}, ST.make(MD.OMT, "blazingClayCraftable", 32, 0));
		}});
		}};
	}}
}