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

package gregtech.loaders.b;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.*;
import static gregapi.data.TD.Compounds.*;
import static gregapi.data.TD.Prefix.*;
import static gregapi.data.TD.Processing.*;
import static gregapi.data.TD.Properties.*;
import static gregapi.oredict.OreDictMaterialCondition.*;

import java.util.ArrayList;
import java.util.List;

import gregapi.block.BlockBase;
import gregapi.block.metatype.BlockStones;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.ICondition;
import gregapi.code.ICondition.And;
import gregapi.config.ConfigCategories;
import gregapi.cover.CoverRegistry;
import gregapi.cover.covers.CoverTextureMulti;
import gregapi.cover.covers.CoverTextureSimple;
import gregapi.data.ANY;
import gregapi.data.CS.BlocksGT;
import gregapi.data.CS.ConfigsGT;
import gregapi.data.CS.OreDictToolNames;
import gregapi.data.CS.SFX;
import gregapi.data.FL;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.data.RM;
import gregapi.data.TD;
import gregapi.item.IPrefixItem;
import gregapi.oredict.IOreDictConfigurationComponent;
import gregapi.oredict.IOreDictListenerEvent;
import gregapi.oredict.IOreDictRecyclableListener;
import gregapi.oredict.OreDictListenerEvent_Names;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.GT_ModHandler;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.render.BlockTextureCopied;
import gregapi.render.BlockTextureDefault;
import gregapi.render.ITexture;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Loader_OreProcessing implements Runnable {
	static final long RECIPE_BITS = CR.DEF_NAC_NCC | CR.ONLY_IF_HAS_RESULT;
	
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void run() {
		OUT.println("GT_Mod: Register Ore processing.");
		
		new OreDictListenerEvent_Names() {@Override public void addAllListeners() {
		
		final ITexture[] tStoneTextures = new ITexture[] {
		  BlockTextureCopied.get(Blocks.stone, 0)
		, BlockTextureCopied.get(Blocks.cobblestone, 0)
		, BlockTextureCopied.get(Blocks.mossy_cobblestone, 0)
		, BlockTextureCopied.get(Blocks.stonebrick, 0)
		, BlockTextureCopied.get(Blocks.stonebrick, 1)
		, BlockTextureCopied.get(Blocks.stonebrick, 2)
		, BlockTextureCopied.get(Blocks.stonebrick, 3)
		, BlockTextureCopied.get(Blocks.double_stone_slab, SIDE_TOP, 0)
		, BlockTextureCopied.get(Blocks.double_stone_slab, SIDE_FRONT, 0)
		};
		addListener(OP.plate.dat(MT.Stone).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureMulti(T, SFX.MC_DIG_ROCK, tStoneTextures));
		}});
		addListener(OP.plate.dat(MT.Netherrack).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureSimple(BlockTextureCopied.get(Blocks.netherrack, 0), SFX.MC_DIG_ROCK));
		}});
		addListener(OP.plate.dat(MT.NetherBrick).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureSimple(BlockTextureCopied.get(Blocks.nether_brick, 0), SFX.MC_DIG_ROCK));
		}});
		addListener(OP.plate.dat(MT.Endstone).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureSimple(BlockTextureCopied.get(Blocks.end_stone, 0), SFX.MC_DIG_ROCK));
		}});
		addListener(OP.plate.dat(MT.Obsidian).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureSimple(BlockTextureCopied.get(Blocks.obsidian, 0), SFX.MC_DIG_ROCK));
		}});
		
		for (BlockBase tBlock : BlocksGT.stones) {
		final BlockStones tStone = (BlockStones)tBlock;
		final ITexture[] tTextures = new ITexture[16];
		for (int i = 0; i < tTextures.length; i++) tTextures[i] = BlockTextureDefault.get(tStone.mIcons[i]);
		
		addListener(OP.plate.dat(tStone.mMaterial).toString(), new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			CoverRegistry.put(aEvent.mStack, new CoverTextureMulti(T, SFX.MC_DIG_ROCK, tTextures));
		}});
		}
		
		}};
		
		plate                       .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, blockSolid, blockPlate, blockIngot, casingMachine, blockDust));
		plateDouble                 .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, casingMachineDouble, blockPlate, blockSolid, blockIngot, blockDust));
		plateTriple                 .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, blockPlate, blockSolid, blockIngot, casingMachineDouble, blockDust));
		plateQuadruple              .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, blockIngot, blockPlate, blockSolid, casingMachineQuadruple, blockDust));
		plateQuintuple              .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, casingMachineQuadruple, blockIngot, blockPlate, blockSolid, blockDust));
		plateDense                  .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, casingMachineDense, blockSolid, blockPlate, blockIngot, blockDust));
		plateCurved                 .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, casingMachine, blockSolid, blockPlate, blockIngot, blockDust));
		plateGem                    .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, blockGem, blockPlateGem, blockDust));
		sheetGt                     .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, blockSolid, blockPlate, blockIngot, casingMachine, blockDust));
		foil                        .addListener(new OreProcessing_CoversMulti((ICondition<OreDictMaterial>)ICondition.TRUE, foil));
		
		scrapGt                     .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		rockGt                      .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dust                        .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dustSmall                   .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dustTiny                    .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dustImpure                  .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dustPure                    .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		dustRefined                 .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		rawOreChunk                 .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		chunk                       .addListener(new OreProcessing_Smelting(U*2, ANTIMATTER.NOT));
		rubble                      .addListener(new OreProcessing_Smelting(U*2, ANTIMATTER.NOT));
		pebbles                     .addListener(new OreProcessing_Smelting(U*2, ANTIMATTER.NOT));
		cluster                     .addListener(new OreProcessing_Smelting(U*2, ANTIMATTER.NOT));
		crushed                     .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		crushedTiny                 .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		crushedPurified             .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		crushedPurifiedTiny         .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		crushedCentrifuged          .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		crushedCentrifugedTiny      .addListener(new OreProcessing_Smelting( -1, ANTIMATTER.NOT));
		
		rawOreChunk                 .addListener(new OreProcessing_Maceration(crushedTiny   , 3, ANTIMATTER.NOT));
		chunk                       .addListener(new OreProcessing_Maceration(dust          , 2, ANTIMATTER.NOT));
		rubble                      .addListener(new OreProcessing_Maceration(dust          , 2, ANTIMATTER.NOT));
		pebbles                     .addListener(new OreProcessing_Maceration(dust          , 2, ANTIMATTER.NOT));
		cluster                     .addListener(new OreProcessing_Maceration(dust          , 2, ANTIMATTER.NOT));
		crushed                     .addListener(new OreProcessing_Maceration(dust          , 1, ANTIMATTER.NOT));
		crushedTiny                 .addListener(new OreProcessing_Maceration(dustTiny      , 1, ANTIMATTER.NOT));
		crushedPurified             .addListener(new OreProcessing_Maceration(dust          , 1, ANTIMATTER.NOT));
		crushedPurifiedTiny         .addListener(new OreProcessing_Maceration(dustTiny      , 1, ANTIMATTER.NOT));
		crushedCentrifuged          .addListener(new OreProcessing_Maceration(dustTiny      ,11, ANTIMATTER.NOT));
		crushedCentrifugedTiny      .addListener(new OreProcessing_Maceration(dustTiny      , 1, ANTIMATTER.NOT));
		
		chemtube                    .addListener(new OreProcessing_GlassTube(-1, ANTIMATTER.NOT));
		
		plantGtBerry                .addListener(new OreProcessing_PlantSqueezing(-1, ANTIMATTER.NOT));
		plantGtTwig                 .addListener(new OreProcessing_PlantSqueezing(-1, ANTIMATTER.NOT));
		plantGtFiber                .addListener(new OreProcessing_PlantSqueezing(-1, ANTIMATTER.NOT));
		plantGtWart                 .addListener(new OreProcessing_PlantSqueezing(-1, ANTIMATTER.NOT));
		plantGtBlossom              .addListener(new OreProcessing_PlantSqueezing(-1, ANTIMATTER.NOT));
		
		dye                         .addListener(new OreProcessing_Dye());
		
		String tCategory = ConfigCategories.Recipes.gregtechrecipes + ".";
		
		if (!MD.QT.mLoaded) {
		ingotDouble                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2ingotDouble"       , new String[][] {{ "h" ,  "I" ,  "I" }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		ingotTriple                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2ingotTriple"       , new String[][] {{ "h" ,  "I" ,  "X" }}, ingotDouble       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		ingotQuadruple              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2ingotQuadruple"    , new String[][] {{ "h" ,  "I" ,  "X" }}, ingotTriple       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		ingotQuintuple              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2ingotQuintuple"    , new String[][] {{ "h" ,  "I" ,  "X" }}, ingotQuadruple    , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateTiny                   .addListener(new OreProcessing_CraftFrom( 1, tCategory + "chunkGt2plateTiny"        , new String[][] {{ "h" ,  "X"        }}, chunkGt           , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plate                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plateCurved2plate"        , new String[][] {{ "h" ,  "X"        }}, plateCurved       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		plate                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2plate"             , new String[][] {{ "h" ,  "X"        }}, ingotDouble       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateDouble                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2plateDouble"       , new String[][] {{ "h" ,  "X"        }}, ingotTriple       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateTriple                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2plateTriple"       , new String[][] {{ "h" ,  "X"        }}, ingotQuadruple    , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateQuadruple              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2plateQuadruple"    , new String[][] {{ "h" ,  "X"        }}, ingotQuintuple    , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateDouble                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingots2plateDouble"       , new String[][] {{ "h" ,  "P" ,  "P" }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateTriple                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plates2plateTriple"       , new String[][] {{ "h" ,  "P" ,  "X" }}, plateDouble       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateQuadruple              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plates2plateQuadruple"    , new String[][] {{ "h" ,  "P" ,  "X" }}, plateTriple       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateQuintuple              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plates2plateQuintuple"    , new String[][] {{ "h" ,  "P" ,  "X" }}, plateQuadruple    , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		plateCurved                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plate2plateCurved"        , new String[][] {{ "h" ,  "P" ,  "z" }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		ring                        .addListener(new OreProcessing_CraftFrom( 1, tCategory + "stick2ring"               , new String[][] {{"hS" , " o"        }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		spring                      .addListener(new OreProcessing_CraftFrom( 1, tCategory + "stick2spring"             , new String[][] {{"zXh"              }}, stickLong         , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		springSmall                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "wire2springSmall"         , new String[][] {{"oXh"              }}, wireGt01          , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		springSmall                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "stick2springSmall"        , new String[][] {{"oSh"              }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		foil                        .addListener(new OreProcessing_CraftFrom( 2, tCategory + "plate2foil"               , new String[][] {{"hPz"              }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, SMITHABLE)));
		casingSmall                 .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plate2casingSmall"        , new String[][] {{"h P"              }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		stickLong                   .addListener(new OreProcessing_CraftFrom( 1, tCategory + "sticks2stickLong"         , new String[][] {{"ShS"              }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		rotor                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "rotor"                    , new String[][] {{"YhY", "TXf", "YdY"}}, ring              , plateCurved   , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		toolHeadBuzzSaw             .addListener(new OreProcessing_CraftFrom( 1, tCategory + "toolHeadBuzzSaw"          , new String[][] {{"wPh", "P P", "fPx"}}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		toolHeadBuzzSaw             .addListener(new OreProcessing_CraftFrom( 1, tCategory + "toolHeadBuzzSaw"          , new String[][] {{"wCh", "C C", "fCx"}}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		}
		
		gearGt                      .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"XYX", "YfY", "XYX"}}, rockGt            , stone         , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, STONE, MT.Stone.NOT, MT.Bedrock.NOT)));
		gearGt                      .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"XYX", "YfY", "XYX"}}, stick             , stone         , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, STONE, MT.Stone.NOT, MT.Bedrock.NOT)));
		gearGt                      .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"XYX", "YwY", "XYX"}}, stick             , plate         , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, SMITHABLE)));
		gearGt                      .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"XYX", "YsY", "XYX"}}, stick             , plank         , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, WOOD)));
		gearGtSmall                 .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"X " , " s"        }}, plank             , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, WOOD)));
		casingMachine               .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"YXX", "XwX", "XXY"}}, plate             , stickLong     , null          , null                          , null                          , ANTIMATTER.NOT));
		casingMachineDouble         .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"YXX", "XwX", "XXY"}}, plateDouble       , stickLong     , null          , null                          , null                          , ANTIMATTER.NOT));
		casingMachineQuadruple      .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"YXX", "XwX", "XXY"}}, plateQuadruple    , stickLong     , null          , null                          , null                          , ANTIMATTER.NOT));
		casingMachineDense          .addListener(new OreProcessing_CraftFrom( 1, null                                   , new String[][] {{"YXX", "XwX", "XXY"}}, plateDense        , stickLong     , null          , null                          , null                          , ANTIMATTER.NOT));
		stickLong                   .addListener(new OreProcessing_CraftFrom( 1, tCategory + "gem2stickLong"            , new String[][] {{"sf" , " X"        }}, gemFlawless       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stickLong                   .addListener(new OreProcessing_CraftFrom( 2, tCategory + "gem2stickLong"            , new String[][] {{"sf" , " X"        }}, gemExquisite      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stickLong                   .addListener(new OreProcessing_CraftFrom( 4, tCategory + "gem2stickLong"            , new String[][] {{"sf" , " X"        }}, gemLegendary      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "ingot2stick"              , new String[][] {{"f " , " I"        }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 2, tCategory + "stickLong2stick"          , new String[][] {{"s " , " X"        }}, stickLong         , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "gem2stick"                , new String[][] {{"s " , "fX"        }}, gem               , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 2, tCategory + "gem2stick"                , new String[][] {{"s " , "fX"        }}, gemFlawless       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 4, tCategory + "gem2stick"                , new String[][] {{"s " , "fX"        }}, gemExquisite      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		stick                       .addListener(new OreProcessing_CraftFrom( 8, tCategory + "gem2stick"                , new String[][] {{"s " , "fX"        }}, gemLegendary      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		bolt                        .addListener(new OreProcessing_CraftFrom( 2, tCategory + "stick2bolt"               , new String[][] {{"s " , " S"        }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		screw                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "bolt2screw"               , new String[][] {{"fX" , "X "        }}, bolt              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		round                       .addListener(new OreProcessing_CraftFrom( 1, tCategory + "chunk2round"              , new String[][] {{"f " , " X"        }}, chunkGt           , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		wireFine                    .addListener(new OreProcessing_CraftFrom( 1, tCategory + "foil2wireFine"            , new String[][] {{"Xx"               }}, foil              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		wireGt01                    .addListener(new OreProcessing_CraftFrom( 1, tCategory + "plate2wire"               , new String[][] {{"Px"               }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateTiny                   .addListener(new OreProcessing_CraftFrom( 8, tCategory + "plate2plateTiny"          , new String[][] {{"s " , " P"        }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT, MT.Paper.NOT)));
		plateGemTiny                .addListener(new OreProcessing_CraftFrom( 8, tCategory + "plate2plateTiny"          , new String[][] {{"s " , " C"        }}, null              , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGemTiny                .addListener(new OreProcessing_CraftFrom( 2, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gemChipped        , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGemTiny                .addListener(new OreProcessing_CraftFrom( 4, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gemFlawed         , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGemTiny                .addListener(new OreProcessing_CraftFrom( 8, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gem               , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGem                    .addListener(new OreProcessing_CraftFrom( 1, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gemFlawless       , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGem                    .addListener(new OreProcessing_CraftFrom( 3, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gemExquisite      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGem                    .addListener(new OreProcessing_CraftFrom( 7, tCategory + "gem2plateGem"             , new String[][] {{"s " , " X"        }}, gemLegendary      , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		plateGem                    .addListener(new OreProcessing_CraftFrom( 3, tCategory + "boule2plateGem"           , new String[][] {{"s " , " X"        }}, bouleGt           , null          , null          , null                          , null                          , new And(ANTIMATTER.NOT, COATED.NOT)));
		minecartWheels              .addListener(new OreProcessing_CraftFrom( 1, tCategory + "minecartWheels"           , new String[][] {{" h ", "XSX", " w "}}, ring              , null          , null          , null                          , null                          , ANTIMATTER.NOT));
		
		arrowGtWood                 .addListener(new OreProcessing_Shapeless( 1, tCategory + "arrowsWooden"             , new Object[] {toolHeadArrow, arrowGtWood.dat(MT.Empty)    }, ANTIMATTER.NOT));
		arrowGtPlastic              .addListener(new OreProcessing_Shapeless( 1, tCategory + "arrowsPlastic"            , new Object[] {toolHeadArrow, arrowGtPlastic.dat(MT.Empty) }, ANTIMATTER.NOT));
		cableGt01                   .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {wireGt01, plate.dat(MT.Rubber)                                         }, ANTIMATTER.NOT));
		cableGt02                   .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {wireGt02, plate.dat(MT.Rubber)                                         }, ANTIMATTER.NOT));
		chemtube                    .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {dustTiny, chemtube.mat(MT.Empty, 1)                                    }, (ICondition<OreDictMaterial>)ICondition.TRUE));
		dustTiny                    .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {chemtube                                                               }, meltmin(DEF_ENV_TEMP)));
		toolHeadRawUniversalSpade   .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {toolHeadShovel     , OreDictToolNames.file, OreDictToolNames.saw       }, new And(ANTIMATTER.NOT, COATED.NOT)));
		toolHeadRawUniversalSpade   .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {toolHeadSpade      , OreDictToolNames.file, OreDictToolNames.saw       }, new And(ANTIMATTER.NOT, COATED.NOT)));
		toolHeadConstructionPickaxe .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {toolHeadRawPickaxe , OreDictToolNames.file, OreDictToolNames.hammer    }, new And(ANTIMATTER.NOT, COATED.NOT)));
		toolHeadPickaxeGem          .addListener(new OreProcessing_Shapeless( 1, null, new Object[] {toolHeadRawPickaxe.dat(ANY.Steel), gemFlawed, gemFlawed, OreDictToolNames.file, OreDictToolNames.hammer, OreDictToolNames.saw}, ANTIMATTER.NOT));
		
		IOreDictListenerEvent tProcessor = new OreProcessing_Ore();
		for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) if (tPrefix.contains(ORE) && tPrefix != oreBedrock && tPrefix != orePoor && tPrefix != oreSmall && tPrefix != oreRich && tPrefix != oreNormal) tPrefix.addListener(tProcessor);
		
		OreDictManager.INSTANCE.addListener(new RecyclingProcessing());
		if (CODE_CLIENT) OreDictManager.INSTANCE.addListener(new RecyclingProcessingCrucibleFakeRecipes());
	}
	
	public static class OreProcessing_CoversSimple implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		public final OreDictPrefix mTargetPrefix;
		
		public OreProcessing_CoversSimple(ICondition<OreDictMaterial> aCondition, OreDictPrefix aTargetPrefix) {
			mTargetPrefix = aTargetPrefix;
			mCondition = aCondition;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				CoverRegistry.put(aEvent.mStack, new CoverTextureSimple(BlockTextureDefault.get(aEvent.mMaterial, mTargetPrefix, aEvent.mMaterial.contains(GLOWING))));
			}
		}
	}
	
	public static class OreProcessing_CoversMulti implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		public final OreDictPrefix[] mTargetPrefixes;
		
		public OreProcessing_CoversMulti(ICondition<OreDictMaterial> aCondition, OreDictPrefix... aTargetPrefixes) {
			mTargetPrefixes = aTargetPrefixes;
			mCondition = aCondition;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial) && ST.block(aEvent.mStack) == NB && CoverRegistry.get(aEvent.mStack) == null) {
				ITexture[] tTextures = new ITexture[mTargetPrefixes.length];
				for (int i = 0; i < tTextures.length; i++) tTextures[i] = BlockTextureDefault.get(aEvent.mMaterial, mTargetPrefixes[i], aEvent.mMaterial.contains(GLOWING));
				CoverRegistry.put(aEvent.mStack, new CoverTextureMulti(aEvent.mMaterial != MT.Paper, tTextures));
			}
		}
	}
	
	public static class RecyclingProcessing implements IOreDictRecyclableListener {
		@Override
		public void onRecycleableRegistration(OreDictRecyclingContainer aEvent) {
			if (aEvent.mItemData == null || ST.container(aEvent.mStack, T) != null || (aEvent.mItemData.mPrefix != null && aEvent.mItemData.mPrefix.containsAny(ORE_PROCESSING_DIRTY, ORE))) return;
			
			List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
			for (OreDictMaterialStack tMaterial : aEvent.mItemData.getAllMaterialStacks()) {
				if (tMaterial.mMaterial == MT.Paper) RM.Mortar.addRecipe1(T, 16, 16, aEvent.mStack, OM.dust(tMaterial));
				if (tMaterial.mMaterial.mTargetSmelting.mAmount > 0 && tMaterial.mMaterial.contains(MELTING) && !tMaterial.mMaterial.contains(BLACKLISTED_SMELTER)) OM.stack(UT.Code.units(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSmelting.mAmount, F), tMaterial.mMaterial.mTargetSmelting.mMaterial).addToList(tList);
			}
			if (tList.isEmpty()) return;
			
			FluidStack tFluid = null;
			OreDictMaterialStack tMaterial = null;
			for (OreDictMaterialStack iMaterial : tList) if (iMaterial.mMaterial.mLiquid != null && UT.Fluids.temperature(iMaterial.mMaterial.mLiquid) < 4618) {
				if (tFluid == null) {
					tMaterial = iMaterial;
					tFluid = iMaterial.mMaterial.liquid(iMaterial.mAmount, F);
				} else {
					tFluid = null;
					break;
				}
			}
			if (tFluid != null && tFluid.amount > 0 && tMaterial != null) {
				RM.Smelter.addRecipe1(T, 16, (long)Math.max(FL.Lava.is(tFluid)?UT.Code.divup(tFluid.amount*EU_PER_LAVA, 16):16, (OM.weight(aEvent.mItemData.getAllMaterialStacks()) * (Math.max(tMaterial.mMaterial.mMeltingPoint, tFluid.getFluid().getTemperature())-DEF_ENV_TEMP))/1600), aEvent.mStack, NF, tFluid, ZL_IS);
			}
		}
	}
	
	public static class RecyclingProcessingCrucibleFakeRecipes implements IOreDictRecyclableListener {
		@Override
		public void onRecycleableRegistration(OreDictRecyclingContainer aEvent) {
			if (aEvent.mItemData == null || (aEvent.mItemData.mPrefix != null && aEvent.mItemData.mPrefix.contains(INGOT_BASED))) return;
			
			List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
			for (OreDictMaterialStack tMaterial : aEvent.mItemData.getAllMaterialStacks()) if (tMaterial.mMaterial.mTargetSmelting.mAmount > 0 && tMaterial.mMaterial.contains(MELTING)) OM.stack(UT.Code.units(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSmelting.mAmount, F), tMaterial.mMaterial.mTargetSmelting.mMaterial).addToList(tList);
			if (tList.isEmpty()) return;
			
			ArrayListNoNulls<ItemStack> tIngots = new ArrayListNoNulls<>();
			for (OreDictMaterialStack tMaterial : tList) tIngots.add(OM.ingotOrDust(tMaterial.mMaterial, tMaterial.mAmount));
			
			if (!tIngots.isEmpty()) RM.CrucibleSmelting.addFakeRecipe(F, new ItemStack[] {aEvent.mStack}, tIngots.toArray(ZL_IS), null, null, null, null, 0, 0, aEvent.mItemData.mMaterial.mMaterial.mMeltingPoint);
		}
	}
	
	public static class OreProcessing_Dye implements IOreDictListenerEvent {
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			int aIndex = 0; while (aIndex < 16) {if (DYE_OREDICTS[aIndex].equals(aEvent.mOreDictName)) break; aIndex++;}
			
			if (aIndex < 16 && ST.container(aEvent.mStack, T) == null) {
				GT_ModHandler.addAlloySmelterRecipe(dust.mat(MT.Glass, 8)       , aEvent.mStack, ST.make(Blocks.stained_glass, 8, 15-aIndex), 200, 8, F);
				GT_ModHandler.addAlloySmelterRecipe(ST.make(Blocks.glass, 8, W) , aEvent.mStack, ST.make(Blocks.stained_glass, 8, 15-aIndex), 200, 8, F);
			}
		}
	}
	
	public static class OreProcessing_Ore implements IOreDictListenerEvent {
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			boolean tIsRich = aEvent.mPrefix.contains(TD.Prefix.DENSE_ORE);
			
			if (aEvent.mMaterial == MT.Oilsands) {
				RM.Centrifuge.addRecipe1(T, 16, tIsRich?512:128, tIsRich?10000:5000, aEvent.mStack, NF, FL.Oil_Normal.make(tIsRich?1000:500), ST.make(Blocks.sand, 1, 0));
			} else {
				registerStandardOreRecipes(aEvent.mPrefix, aEvent.mMaterial, aEvent.mStack, aEvent.mMaterial.mOreProcessingMultiplier * (tIsRich?2:1));
			}
		}
		
		private ArrayList<OreDictMaterial> mAlreadyListedOres = new ArrayListNoNulls<>(1000);
		
		private boolean registerStandardOreRecipes(OreDictPrefix aPrefix, OreDictMaterial aMaterial, ItemStack aOreStack, int aMultiplier) {
			if (aOreStack == null || aMaterial == null) return F;
			if (COMPAT_IC2 != null && !(aOreStack.getItem() instanceof IPrefixItem)) COMPAT_IC2.valuable(ST.block(aOreStack), ST.meta(aOreStack), 3);
			aMaterial = aMaterial.mTargetCrushing.mMaterial;
			if (aMaterial == null) return F;
			OreDictMaterial tPrimaryByMaterial = null, tSecondaryByMaterial = null, tTertiaryByMaterial = null;
			aOreStack = ST.amount(1, aOreStack);
			
			ItemStack
			tGem = gem.mat(aMaterial, 1),
			tSmeltInto = OM.ingot(aMaterial.mTargetSmelting),
			tTiny = OM.dustOrIngot(aMaterial, U9),
			tSmall = OM.dustOrIngot(aMaterial, U4),
			tDust = dust.mat(aMaterial, tGem, 1),
			tCleaned = crushedPurified.mat(aMaterial, tDust, 1),
			tCrushed = crushed.mat(aMaterial, UT.Code.bindStack(aMaterial.mOreMultiplier * aMultiplier)),
			tPrimaryByProduct   = null, tPrimaryByProductSmall      = null, tPrimaryByProductTiny   = null,
			tSecondaryByProduct = null, tSecondaryByProductSmall    = null, tSecondaryByProductTiny = null,
			tTertiaryByProduct  = null, tTertiaryByProductSmall     = null, tTertiaryByProductTiny  = null;
			
			if (aMaterial == MT.Gneiss || aMaterial == MT.PetrifiedWood) {
				RM.Crusher  .addRecipe1(T, 16, 64, aOreStack, OP.rockGt.mat(aMaterial, UT.Code.bindStack(aMaterial.mOreMultiplier * aMultiplier * 4)));
				RM.Hammer   .addRecipe1(T, 16, 64, aOreStack, OP.rockGt.mat(aMaterial, UT.Code.bindStack(aMaterial.mOreMultiplier * aMultiplier * 3)));
			}
			
			if (tSmeltInto == null) tSmeltInto = OM.gem(aMaterial.mTargetSmelting);
			if (tCrushed == null) tCrushed = dustImpure.mat(aMaterial, ST.amount(UT.Code.bindStack(aMaterial.mOreMultiplier * aMultiplier), tCleaned, tDust, tGem), aMaterial.mOreMultiplier * aMultiplier);
			
			ArrayList<ItemStack> tByProductStacks = new ArrayListNoNulls<>();
			
			for (OreDictMaterial tMat : aMaterial.mByProducts) {
				ItemStack tByProduct = OM.dustOrIngot(tMat, U);
				if (tByProduct != null) tByProductStacks.add(tByProduct);
				if (tPrimaryByProduct == null) {
					tPrimaryByMaterial = tMat;
					tPrimaryByProduct = OM.dustOrIngot(tMat, U);
					tPrimaryByProductSmall = OM.dustOrIngot(tMat, U4);
					tPrimaryByProductTiny = OM.dustOrIngot(tMat, U9);
				}
				if (tSecondaryByProduct == null || tSecondaryByMaterial == tPrimaryByMaterial) {
					tSecondaryByMaterial = tMat;
					tSecondaryByProduct = OM.dustOrIngot(tMat, U);
					tSecondaryByProductSmall = OM.dustOrIngot(tMat, U4);
					tSecondaryByProductTiny = OM.dustOrIngot(tMat, U9);
				}
				if (tTertiaryByProduct == null || tTertiaryByMaterial == tPrimaryByMaterial || tTertiaryByMaterial == tSecondaryByMaterial) {
					tTertiaryByMaterial = tMat;
					tTertiaryByProduct = OM.dustOrIngot(tMat, U);
					tTertiaryByProductSmall = OM.dustOrIngot(tMat, U4);
					tTertiaryByProductTiny = OM.dustOrIngot(tMat, U9);
				}
			}
			
			if (!tByProductStacks.isEmpty() && !mAlreadyListedOres.contains(aMaterial)) {
				mAlreadyListedOres.add(aMaterial);
				RM.ByProductList.addFakeRecipe(F, new ItemStack[] {oreVanillastone.mat(aMaterial, aOreStack, 1), dustImpure.mat(aMaterial, 1), dustPure.mat(aMaterial, 1), crushed.mat(aMaterial, 1), crushedPurified.mat(aMaterial, 1), crushedCentrifuged.mat(aMaterial, 1)}, tByProductStacks.toArray(ZL_IS), null, null, null, null, 0, 0, 0);
			}
			
			if (tPrimaryByMaterial == null) tPrimaryByMaterial = aMaterial;
			if (tPrimaryByProduct == null) tPrimaryByProduct = tDust;
			if (tPrimaryByProductSmall == null) tPrimaryByProductSmall = tSmall;
			if (tPrimaryByProductTiny == null) tPrimaryByProductTiny = tTiny;
			
			if (tSecondaryByMaterial == null) tSecondaryByMaterial = tPrimaryByMaterial;
			if (tSecondaryByProduct == null) tSecondaryByProduct = tPrimaryByProduct;
			if (tSecondaryByProductSmall == null) tSecondaryByProductSmall = tPrimaryByProductSmall;
			if (tSecondaryByProductTiny == null) tSecondaryByProductTiny = tPrimaryByProductTiny;
			
			if (tTertiaryByMaterial == null) tTertiaryByMaterial = tSecondaryByMaterial;
			if (tTertiaryByProduct == null) tTertiaryByProduct = tSecondaryByProduct;
			if (tTertiaryByProductSmall == null) tTertiaryByProductSmall = tSecondaryByProductSmall;
			if (tTertiaryByProductTiny == null) tTertiaryByProductTiny = tSecondaryByProductTiny;
			
			if (tSmeltInto != null && aMaterial.contains(FURNACE)) {
				RM.add_smelting(aOreStack, tSmeltInto);
			} else {
				RM.rem_smelting(aOreStack);
			}
			
			if (tCrushed != null && tCleaned != null && aPrefix.contains(DUST_ORE)) {
				RM.Sifting.addRecipe1(T, 16, 256, new long[] {10000, 1500, 1000, 500}, aOreStack, ST.amount(UT.Code.units(aMaterial.mTargetCrushing.mAmount, U, tCrushed.stackSize * 2, F), tCleaned), ST.amount(tCrushed.stackSize, tPrimaryByProductTiny), ST.amount(tCrushed.stackSize, tSecondaryByProductTiny), ST.amount(tCrushed.stackSize, tTertiaryByProductTiny));
			}
			return T;
		}
	}
	
	public static class OreProcessing_Maceration implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final OreDictPrefix mTargetPrefix;
		private final long mAmount;
		
		public OreProcessing_Maceration(OreDictPrefix aTargetPrefix, long aAmount, ICondition<OreDictMaterial> aCondition) {
			mTargetPrefix = aTargetPrefix;
			mCondition = aCondition;
			mAmount = aAmount;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				RM.pulverizing(aEvent.mStack, mTargetPrefix.mat(aEvent.mMaterial, mAmount));
			}
		}
	}
	
	public static class OreProcessing_GlassTube implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final long mMaterialAmount;
		
		public OreProcessing_GlassTube(long aOutputMaterialAmount, ICondition<OreDictMaterial> aCondition) {
			mCondition = aCondition;
			mMaterialAmount = aOutputMaterialAmount;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial) && aEvent.mMaterial != MT.Empty) {
				FluidStack tFluid = aEvent.mMaterial.fluid(DEF_ENV_TEMP, mMaterialAmount<0?aEvent.mPrefix.mAmount:mMaterialAmount, F);
				ItemStack  tStack = OM.dust(aEvent.mMaterial, mMaterialAmount<0?aEvent.mPrefix.mAmount:mMaterialAmount);
				if (tFluid == null || tFluid.amount <= 0 || FL.Error.is(tFluid)) tFluid = null;
				if (tStack != null || tFluid != null) {
					RM.Canner.addRecipe1(T, 16, 16, aEvent.mStack, NF, tStack==null?tFluid:NF, aEvent.mPrefix.mat(MT.Empty, 1), tStack);
					RM.Canner.addRecipe2(T, 16, 16, tStack, aEvent.mPrefix.mat(MT.Empty, 1), tStack==null?tFluid:NF, NF, aEvent.mStack);
				}
			}
		}
	}
	
	public static class OreProcessing_PlantSqueezing implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final long mOutputMaterialAmount;
		
		public OreProcessing_PlantSqueezing(long aOutputMaterialAmount, ICondition<OreDictMaterial> aCondition) {
			mCondition = aCondition;
			mOutputMaterialAmount = aOutputMaterialAmount;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				FluidStack tFluid = aEvent.mMaterial.fluid(DEF_ENV_TEMP, mOutputMaterialAmount<0?aEvent.mPrefix.mAmount:mOutputMaterialAmount, F);
				ItemStack  tStack = OM.dust(aEvent.mMaterial, mOutputMaterialAmount<0?aEvent.mPrefix.mAmount:mOutputMaterialAmount);
				if (tFluid == null || tFluid.amount <= 0 || FL.Error.is(tFluid)) tFluid = null;
				if (tStack != null || tFluid != null) {
					RM.Squeezer.addRecipe1(T, 16, UT.Code.units(aEvent.mPrefix.mAmount, U, 256+256*aEvent.mMaterial.mToolQuality, T), aEvent.mStack, NF, tFluid, tFluid==null?tStack:null);
				}
			}
		}
	}
	
	public static class OreProcessing_Welding implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final OreDictPrefix mTargetPrefix;
		private final long mInputAmount, mOutputAmount;
		
		public OreProcessing_Welding(OreDictPrefix aTargetPrefix, long aInputAmount, long aOutputAmount, ICondition<OreDictMaterial> aCondition) {
			mTargetPrefix = aTargetPrefix;
			mCondition = aCondition;
			mInputAmount = aInputAmount;
			mOutputAmount = aOutputAmount;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				ItemStack tStack = mTargetPrefix.mat(aEvent.mMaterial, mOutputAmount);
				if (tStack != null) {
					RM.Welder.addRecipe2(T, 16, UT.Code.units(aEvent.mPrefix.mAmount*mInputAmount, U,  64+64*aEvent.mMaterial.mToolQuality, T), ST.tag(mInputAmount), ST.amount(mInputAmount, aEvent.mStack), tStack);
				}
			}
		}
	}
	
	public static class OreProcessing_Decomposition implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final RecipeMap mTargetRecipeMap;
		
		public OreProcessing_Decomposition(RecipeMap aTargetRecipeMap, ICondition<OreDictMaterial> aCondition) {
			mCondition = aCondition;
			mTargetRecipeMap = aTargetRecipeMap;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				IOreDictConfigurationComponent tComponents = aEvent.mMaterial.mComponents;
				if (tComponents != null && tComponents.getCommonDivider() <= 64) {
					ArrayListNoNulls<ItemStack> tStackOutputs = new ArrayListNoNulls<>();
					ArrayListNoNulls<FluidStack> tFluidOutputs = new ArrayListNoNulls<>();
					long tAmount = 0;
					
					for (OreDictMaterialStack tMaterial : tComponents.getUndividedComponents()) {
						tAmount += tMaterial.mAmount;
						if (tMaterial.mMaterial.mMeltingPoint <= DEF_ENV_TEMP && tFluidOutputs.add(tMaterial.mMaterial.fluid(DEF_ENV_TEMP, tMaterial.mAmount, F))) continue;
						if (tStackOutputs.add(OM.dust(tMaterial.mMaterial, tMaterial.mAmount))) continue;
					}
					
					while (tStackOutputs.size() > mTargetRecipeMap.mOutputItemsCount) tStackOutputs.remove(tStackOutputs.size() - 1);
					while (tFluidOutputs.size() > mTargetRecipeMap.mOutputFluidCount) tFluidOutputs.remove(tFluidOutputs.size() - 1);
					
					if (tStackOutputs.size() > 0 || tFluidOutputs.size() > 0) mTargetRecipeMap.addRecipe(T, new ItemStack[] {ST.amount(tComponents.getCommonDivider(), aEvent.mStack)}, tStackOutputs.toArray(ZL_IS), NI, ZL_LONG, ZL_FS, tFluidOutputs.toArray(ZL_FS), UT.Code.units(tAmount, U, 256, T), (tAmount * 16) / U, 0);
				}
			}
		}
	}
	
	public static class OreProcessing_Smelting implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final long mTargetAmount;
		
		public OreProcessing_Smelting(long aTargetAmount, ICondition<OreDictMaterial> aCondition) {
			mTargetAmount = aTargetAmount;
			mCondition = aCondition;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (mCondition.isTrue(aEvent.mMaterial)) {
				if (aEvent.mMaterial.contains(FURNACE)) {
					RM.add_smelting(aEvent.mStack, OM.ingot(aEvent.mMaterial.mTargetSmelting.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units(UT.Code.units(aEvent.mMaterial.mTargetSmelting.mAmount, U, aEvent.mMaterial.mTargetSmelting.mMaterial.mTargetSolidifying.mAmount, F), U, mTargetAmount<0?aEvent.mPrefix.mAmount:mTargetAmount, F)));
				} else {
					RM.rem_smelting(aEvent.mStack);
				}
			}
		}
	}
	
	public static class OreProcessing_Shapeless implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final String mCategoryName;
		private final Object[] mRecipe;
		private final long mOutputAmount;
		
		/**
		 * Converts the Prefixes inside the aRecipe Array automatically to aRecipe[i].get(aEvent.mMaterial)
		 */
		public OreProcessing_Shapeless(long aOutputAmount, String aCategoryName, Object[] aRecipe, ICondition<OreDictMaterial> aCondition) {
			mOutputAmount = aOutputAmount;
			mRecipe = aRecipe;
			mCondition = aCondition;
			mCategoryName = aCategoryName;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (aEvent.mNotAlreadyRegisteredName && mCondition.isTrue(aEvent.mMaterial) && (mCategoryName == null || ConfigsGT.RECIPES.get(mCategoryName, aEvent.mMaterial.mNameInternal, T))) {
				Object[] tRecipe = new Object[mRecipe.length];
				for (int i = 0; i < tRecipe.length; i++) {
					if (mRecipe[i] instanceof OreDictPrefix) {
						tRecipe[i] = ((OreDictPrefix)mRecipe[i]).dat(aEvent.mMaterial);
					} else {
						tRecipe[i] = mRecipe[i];
					}
				}
				CR.shapeless(ST.amount(mOutputAmount, aEvent.mStack), RECIPE_BITS, tRecipe);
			}
		}
	}
	
	public static class OreProcessing_CraftFrom implements IOreDictListenerEvent {
		private final ICondition<OreDictMaterial> mCondition;
		private final String[][] mRecipes;
		private final String mCategoryName;
		private final OreDictPrefix mSpecialPrefix1, mSpecialPrefix2, mSpecialPrefix3;
		private final Object mSpecialObject1, mSpecialObject2;
		private final long mOutputAmount;
		
		/**
		 * V = Special Object 1
		 * W = Special Object 2
		 * X = Special Prefix 1
		 * Y = Special Prefix 2
		 * Z = Special Prefix 3
		 * I = ingot
		 * P = plate
		 * G = gem
		 * C = plateGem
		 * T = screw
		 * S = stick
		 * H = stick, made of Handle Material
		 */
		public OreProcessing_CraftFrom(long aOutputAmount, String aCategoryName, String[][] aRecipes, OreDictPrefix aSpecialPrefix1, OreDictPrefix aSpecialPrefix2, OreDictPrefix aSpecialPrefix3, Object aSpecialObject1, Object aSpecialObject2, ICondition<OreDictMaterial> aCondition) {
			mSpecialPrefix1 = aSpecialPrefix1;
			mSpecialPrefix2 = aSpecialPrefix2;
			mSpecialPrefix3 = aSpecialPrefix3;
			mSpecialObject1 = aSpecialObject1;
			mSpecialObject2 = aSpecialObject2;
			mOutputAmount = aOutputAmount;
			mRecipes = aRecipes;
			mCondition = aCondition;
			mCategoryName = aCategoryName;
		}
		
		@Override
		public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			if (aEvent.mNotAlreadyRegisteredName && mCondition.isTrue(aEvent.mMaterial) && (mCategoryName == null || ConfigsGT.RECIPES.get(mCategoryName, aEvent.mMaterial.mNameInternal, T))) {
				for (int i = 0; i < mRecipes.length; i++) if (mRecipes[i] != null && mRecipes[i].length > 0) {
						 if (mRecipes[i].length == 1)   CR.shaped(aEvent.mPrefix.mat(aEvent.mMaterial, mOutputAmount, ST.amount(mOutputAmount, aEvent.mStack))  , RECIPE_BITS, new Object[] {mRecipes[i][0]                                 , 'G', gem.dat(aEvent.mMaterial), 'I', aEvent.mMaterial==MT.Wood?OD.plankWood:ingot.dat(aEvent.mMaterial), 'P', aEvent.mMaterial==MT.Wood?OD.plankWood:plate.dat(aEvent.mMaterial), 'C', plateGem.dat(aEvent.mMaterial), 'S', stick.dat(aEvent.mMaterial), 'T', screw.dat(aEvent.mMaterial), 'N', nugget.dat(aEvent.mMaterial), 'V', mSpecialObject1==null?plate.dat(aEvent.mMaterial):mSpecialObject1, 'W', mSpecialObject2==null?plate.dat(aEvent.mMaterial):mSpecialObject2, 'X', (mSpecialPrefix1==null?plate:mSpecialPrefix1).dat(aEvent.mMaterial), 'Y', (mSpecialPrefix2==null?plate:mSpecialPrefix2).dat(aEvent.mMaterial), 'Z', (mSpecialPrefix3==null?plate:mSpecialPrefix3).dat(aEvent.mMaterial), 'H', stick.dat(aEvent.mMaterial.mHandleMaterial)});
					else if (mRecipes[i].length == 2)   CR.shaped(aEvent.mPrefix.mat(aEvent.mMaterial, mOutputAmount, ST.amount(mOutputAmount, aEvent.mStack))  , RECIPE_BITS, new Object[] {mRecipes[i][0], mRecipes[i][1]                 , 'G', gem.dat(aEvent.mMaterial), 'I', aEvent.mMaterial==MT.Wood?OD.plankWood:ingot.dat(aEvent.mMaterial), 'P', aEvent.mMaterial==MT.Wood?OD.plankWood:plate.dat(aEvent.mMaterial), 'C', plateGem.dat(aEvent.mMaterial), 'S', stick.dat(aEvent.mMaterial), 'T', screw.dat(aEvent.mMaterial), 'N', nugget.dat(aEvent.mMaterial), 'V', mSpecialObject1==null?plate.dat(aEvent.mMaterial):mSpecialObject1, 'W', mSpecialObject2==null?plate.dat(aEvent.mMaterial):mSpecialObject2, 'X', (mSpecialPrefix1==null?plate:mSpecialPrefix1).dat(aEvent.mMaterial), 'Y', (mSpecialPrefix2==null?plate:mSpecialPrefix2).dat(aEvent.mMaterial), 'Z', (mSpecialPrefix3==null?plate:mSpecialPrefix3).dat(aEvent.mMaterial), 'H', stick.dat(aEvent.mMaterial.mHandleMaterial)});
					else                                CR.shaped(aEvent.mPrefix.mat(aEvent.mMaterial, mOutputAmount, ST.amount(mOutputAmount, aEvent.mStack))  , RECIPE_BITS, new Object[] {mRecipes[i][0], mRecipes[i][1], mRecipes[i][2] , 'G', gem.dat(aEvent.mMaterial), 'I', aEvent.mMaterial==MT.Wood?OD.plankWood:ingot.dat(aEvent.mMaterial), 'P', aEvent.mMaterial==MT.Wood?OD.plankWood:plate.dat(aEvent.mMaterial), 'C', plateGem.dat(aEvent.mMaterial), 'S', stick.dat(aEvent.mMaterial), 'T', screw.dat(aEvent.mMaterial), 'N', nugget.dat(aEvent.mMaterial), 'V', mSpecialObject1==null?plate.dat(aEvent.mMaterial):mSpecialObject1, 'W', mSpecialObject2==null?plate.dat(aEvent.mMaterial):mSpecialObject2, 'X', (mSpecialPrefix1==null?plate:mSpecialPrefix1).dat(aEvent.mMaterial), 'Y', (mSpecialPrefix2==null?plate:mSpecialPrefix2).dat(aEvent.mMaterial), 'Z', (mSpecialPrefix3==null?plate:mSpecialPrefix3).dat(aEvent.mMaterial), 'H', stick.dat(aEvent.mMaterial.mHandleMaterial)});
				}
			}
		}
	}
	
}
