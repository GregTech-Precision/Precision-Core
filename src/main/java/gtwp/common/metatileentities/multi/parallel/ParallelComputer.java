package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.Widget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.IReceiver;
import gtwp.api.capability.impl.ParallelComputerLogic;
import gtwp.api.capability.impl.ParallelRecipeLogic;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.common.blocks.BlockCasing;
import gtwp.common.blocks.GTWPMetaBlocks;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import scala.collection.Parallel;

import javax.annotation.Nonnull;
import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class ParallelComputer extends MultiblockWithDisplayBase {

    public ParallelComputer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentString("Satellite is " + (isReceivingSignal() ? "online" : "offline")));
        textList.add(new TextComponentString("Current parallel points: "+getCurrentParallelPoints()));
        super.addDisplayText(textList);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(LEFT, UP, FRONT)
                .aisle("CC", "CS", "CC", "CC")
                .aisle("CC", "RT", "RT", "CC").setRepeatable(2, 16)
                .aisle("CC", "CC", "CC", "CC")
                .where('S', selfPredicate())
                .where('C', states(casingState()).or(autoAbilities(true, false)).or(abilities(GTWPMultiblockAbility.RECEIVER).setMaxGlobalLimited(1,1)).or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(1)))
                .where('R', metaTileEntities(GTWPMetaTileEntities.PARALLEL_RACK))
                .where('T', metaTileEntities(GTWPMetaTileEntities.PARALLEL_TRANSMITTER))
                .build();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    public int getCurrentParallelPoints(){
        int parallelPoints = 0;
        for(IMultiblockPart imp : getMultiblockParts())
            if(imp instanceof ParallelComputerRack)
                parallelPoints += ((ParallelComputerRack) imp).getParallelPoints();
        return parallelPoints;
    }

    private IBlockState casingState(){
        return GTWPMetaBlocks.CASING.getState(BlockCasing.Casings.COMPUTER);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTWPTextures.COMPUTER_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputer(metaTileEntityId);
    }

    public boolean isReceivingSignal() {
        if (isActive()) {
            List<IReceiver> receivers = getAbilities(GTWPMultiblockAbility.RECEIVER);
            return !receivers.isEmpty() && receivers.get(0).isConnected() && ((SatelliteReceiver) receivers.get(0)).getConnection().isTransmitting();
        }
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
