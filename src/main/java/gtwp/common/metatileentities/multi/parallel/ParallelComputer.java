package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gtwp.api.capability.IReceiver;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ParallelComputer extends MultiblockWithDisplayBase {

    public ParallelComputer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CC", "RT", "RT", "CC").setRepeatable(1, 15)
                .aisle("FF", "SF", "FF", "FF")
                .where('S', selfPredicate())
                .where('C', states(casingState()))
                .where('F', states(casingState()).or(autoAbilities(true, false)).or(metaTileEntities(GTWPMetaTileEntities.RECEIVER)))
                .where('R', metaTileEntities(GTWPMetaTileEntities.PARALLEL_RACK)) //parallel rack
                .where('T', metaTileEntities(GTWPMetaTileEntities.PARALLEL_TRANSMITTER)) //parallel transmitter
                .build();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    private IBlockState casingState(){
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputer(metaTileEntityId);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean hasFrontFacing() {
        return true;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }
}
