package gtwp.metatileentities.multi;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.blocks.BlockIGlass;
import gtwp.recipes.GTWPRecipeMaps;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class Greenhouse extends RecipeMapMultiblockController {

    public Greenhouse(ResourceLocation metaTileEntityId){
        super(metaTileEntityId, GTWPRecipeMaps.GREENHOUSE);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#CCC#", "#####")
                .aisle("C***C", "#GGG#").setRepeatable(3)
                .aisle("#CSC#", "#####")
                .where('S', selfPredicate())
                .where('G', new TraceabilityPredicate(glassPredicate()))
                .where('C', new TraceabilityPredicate(glassPredicate())
                        .or(autoAbilities(true, false, true, true, true, false, false)))
                .where('*', air())
                .where('#', any())
                .build();
    }

    public static Predicate<BlockWorldState> glassPredicate() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockIGlass;
        };
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new Greenhouse(metaTileEntityId);
    }
}
