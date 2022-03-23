package gtwp.common.metatileentities.multi.processing;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gtwp.api.metatileentities.GTWPRecipeMapMultiblockController;
import gtwp.api.render.GTWPTextures;
import gtwp.common.blocks.BlockCasing;
import gtwp.common.blocks.BlockIGlass;
import gtwp.common.blocks.GTWPMetaBlocks;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class Extradification extends GTWPRecipeMapMultiblockController {


    public Extradification(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap<?>[]{RecipeMaps.EXTRACTOR_RECIPES, RecipeMaps.FLUID_SOLIDFICATION_RECIPES, RecipeMaps.FLUID_HEATER_RECIPES });
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#CCC#", "C###C", "C###C", "C###C", "#CCC#")
                .aisle("CCCCC", "#CGC#", "#CGC#", "#CGC#", "CCGCC")
                .aisle("CCCCC", "#G*G#", "#G*G#", "#G*G#", "CGCGC")
                .aisle("CCCCC", "#CGC#", "#CGC#", "#CGC#", "CCGCC")
                .aisle("#CSC#", "C###C", "C###C", "C###C", "#CCC#")
                .where('S', selfPredicate())
                .where('C', states(GTWPMetaBlocks.CASING.getState(BlockCasing.Casings.EXTRADIFICATION)).or(autoAbilities(true, true, true, true, true, true, false).setMaxGlobalLimited(9)))
                .where('G', new TraceabilityPredicate(glassPredicate()))
                .where('#', any())
                .where('*', air())
                .build();
    }

    public static Predicate<BlockWorldState> glassPredicate() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockIGlass;
        };
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTWPTextures.CASING_EXTRADIFICATION;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new Extradification(metaTileEntityId);
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }
}
