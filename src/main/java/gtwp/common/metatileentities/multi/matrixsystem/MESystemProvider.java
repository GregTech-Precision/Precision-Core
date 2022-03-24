package gtwp.common.metatileentities.multi.matrixsystem;

import appeng.block.crafting.BlockCraftingStorage;
import appeng.block.crafting.BlockCraftingUnit;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gtwp.api.metatileentities.GTWPRecipeMapMultiblockController;
import gtwp.api.recipes.GTWPRecipeMaps;
import gtwp.api.render.GTWPTextures;
import gtwp.common.blocks.BlockCasing;
import gtwp.common.blocks.BlockIGlass;
import gtwp.common.blocks.GTWPMetaBlocks;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class MESystemProvider extends GTWPRecipeMapMultiblockController {

    public MESystemProvider(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTWPRecipeMaps.ME_SYSTEM_PROVIDER);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC#", "CGGGC#", "CGGGC#", "CGGGC#")
                .aisle("CCCCCC", "CUSUCC", "CUSUCC", "CGGGC#")
                .aisle("CCCCCC", "CUSUCS", "CUSUCC", "CGGGC#")
                .aisle("CCCCC#", "CGMGC#", "CGGGC#", "CGGGC#")
                .where('S', selfPredicate())
                .where('C', states(GTWPMetaBlocks.CASING.getState(BlockCasing.Casings.ME)).or(autoAbilities(true, true, true, true, true, false, false).setMaxGlobalLimited(9)))
                .where('G', new TraceabilityPredicate(glassPredicate()))
                .where('M', metaTileEntities(GTWPMetaTileEntities.AE_CONNECTOR))
                .where('U', new TraceabilityPredicate(craftingUnit()))
                .where('S', new TraceabilityPredicate(craftingStorage()))
                .where('#', any())
                .build();
    }

    public static Predicate<BlockWorldState> glassPredicate() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockIGlass;
        };
    }

    public static Predicate<BlockWorldState> craftingStorage() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockCraftingStorage;
        };
    }

    public static Predicate<BlockWorldState> craftingUnit() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockCraftingUnit;
        };
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTWPTextures.CASING_ME;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MESystemProvider(metaTileEntityId);
    }
}
