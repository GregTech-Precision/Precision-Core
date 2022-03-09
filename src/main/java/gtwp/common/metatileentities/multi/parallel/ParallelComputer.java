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
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.IReceiver;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.common.blocks.BlockCasing;
import gtwp.common.blocks.GTWPMetaBlocks;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class ParallelComputer extends MultiblockWithDisplayBase {

    public ParallelComputer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CC", "RT", "RT", "CC").setRepeatable(1, 14)
                .aisle("CC", "SC", "CC", "CC")
                .where('S', selfPredicate())
                .where('C', states(casingState()).or(autoAbilities(true, false)).or(abilities(GTWPMultiblockAbility.RECEIVER).setMaxGlobalLimited(1).setPreviewCount(1)))
                .where('R', metaTileEntities(GTWPMetaTileEntities.PARALLEL_RACK)) //parallel rack
                .where('T', metaTileEntities(GTWPMetaTileEntities.PARALLEL_TRANSMITTER)) //parallel transmitter
                .build();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    private IBlockState casingState(){
        return GTWPMetaBlocks.CASING.getState(BlockCasing.Casings.PARALLEL);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputer(metaTileEntityId);
    }

    public boolean isReceivingSignal(){
        List<IReceiver> receivers = getAbilities(GTWPMultiblockAbility.RECEIVER);
        if(!receivers.isEmpty()){
            if(receivers.get(0).isConnected())
                return ((SatelliteReceiver) receivers.get(0)).getConnection().isTransmitting();
        }
        return false;
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
