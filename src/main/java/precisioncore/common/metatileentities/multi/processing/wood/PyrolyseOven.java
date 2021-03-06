package precisioncore.common.metatileentities.multi.processing.wood;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class PyrolyseOven extends PrecisionRecipeMapMultiblockController {

    public PyrolyseOven(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PrecisionRecipeMaps.PYROLYSE);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("F#F#F###F", "CCC#CCCCC", "CCC#CCCCC")
                .aisle("####CCCCC", "CPPPP***C", "CCC#CCCCC")
                .aisle("F#F#F###F", "SCC#CCCCC", "CCC#CCCCC")
                .where('S', selfPredicate())
                .where('P', states(getPipeCasing()))
                .where('F', states(getFrameState()))
                .where('C', states(getCasingState()).or(autoAbilities(true, true, true, true,false, true, false).setMaxGlobalLimited(6)))
                .where('#', any())
                .where('*', air())
                .build();
    }

    private IBlockState getCasingState(){
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private IBlockState getPipeCasing(){
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    private IBlockState getFrameState(){
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new PyrolyseOven(metaTileEntityId);
    }
}
