package gtwp.metatileentities.longpipes;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.sun.jmx.remote.internal.ArrayQueue;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.Textures;
import gtwp.blocks.BlockPipeline;
import gtwp.blocks.GTWPMetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import scala.tools.cmd.Meta;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;


public class PipelineBase extends MetaTileEntity {

    private PipelineBase pair = null;

    public PipelineBase(ResourceLocation metaTileEntityId)
    {
        super(metaTileEntityId);
    }

    public boolean isPaired()
    {
        return pair != null;
    }

    public boolean isValidPair(PipelineBase validation)
    {
        return pair == validation;
    }

    public void setPair(@Nonnull PipelineBase newPair){

    }

    public void invalidatePair() {

    }

    public PipelineBase getPair() {
        if(!isPaired())
            findPair(getPos(), new ArrayDeque<>());
        return pair;
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        getPair();
    }

    public void findPair(BlockPos pos, Queue<BlockPos> visited)
    {
        if(!isPaired()) {
            visited.add(pos);
            for (EnumFacing to : EnumFacing.VALUES) {
                BlockPos nextPos = pos.offset(to);
                if (!visited.contains(nextPos) && !getWorld().isAirBlock(nextPos)) {
                    Block pipe = getWorld().getBlockState(nextPos).getBlock();
                    if (pipe instanceof BlockPipeline) {
                        if(((BlockPipeline) pipe).getState(pipe.getBlockState()).)
                        findPair(nextPos, visited);
                    } else {
                        TileEntity te = getWorld().getTileEntity(nextPos);
                        if (te instanceof MetaTileEntityHolder) {
                            MetaTileEntity mte = ((MetaTileEntityHolder) te).getMetaTileEntity();
                            if(mte instanceof PipelineBase)
                            {
                                PipelineBase newPair = ((PipelineBase) mte);
                                if(newPair != this) {
                                    setPair(newPair);
                                    pair.setPair(this);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();

    }

    @Override
    public void update() {
        super.update();
        if(isPaired() && getOffsetTimer() % 20 == 18) GTLog.logger.info("paired");
    }

    public boolean checkPipeType()
    {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineBase(metaTileEntityId);
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing().getOpposite(), renderState, translation, pipeline);
        Textures.PIPE_OUT_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }
}
