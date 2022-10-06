package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.PrecisionConfigHolder;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;
import precisioncore.common.metatileentities.PrecisionMetaTileEntities;

import javax.annotation.Nonnull;

public class Satellite extends MultiblockWithDisplayBase {

    public Satellite(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("*C*C*", "*C*C*", "*C*C*", "*C*C*")
                .aisle("CCCCC", "CCCCC", "CCCCC", "*C*C*")
                .aisle("CCCCC", "CCSCC", "CCCCC", "*C*C*")
                .aisle("*C*C*", "*C*C*", "*C*C*", "*C*C*")
                .where('S', selfPredicate())
                .where('C', BlockCasing.predicate(BlockCasing.Casings.SATELLITE)
                        .or(metaTileEntities(PrecisionMetaTileEntities.SATELLITE_TRANSMITTER)
                                .setMaxGlobalLimited(1,1)))
                .where('*', any())
                .build();
    }

    private IBlockState casingState(){
        return PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.SATELLITE);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.SATELLITE_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new Satellite(metaTileEntityId);
    }

    public boolean isAbleToWork(){
        return getWorld().provider.getDimension() == (PrecisionConfigHolder.machineOptions.devMode ? 0 : ( PrecisionConfigHolder.machineOptions.satelliteRequireMoon ? -28 : -27));
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
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
