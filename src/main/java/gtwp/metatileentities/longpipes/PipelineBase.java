package gtwp.metatileentities.longpipes;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.Textures;
import gtwp.blocks.BlockPipeline;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Queue;


public abstract class PipelineBase extends MetaTileEntity {

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
        pair = newPair;
    }

    public void invalidatePair() {
        pair = null;
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
                        if(checkPipeType(((BlockPipeline) pipe), nextPos))
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

    public boolean isInput(){
        if(!getWorld().isAirBlock(getPos().offset(getFrontFacing().getOpposite()))) {
            return getWorld().getBlockState(getPos().offset(getFrontFacing().getOpposite())).getBlock() instanceof BlockPipeline;
        }
        return false;
    }

    public TileEntity getInput(){
        if(isInput() && isPaired()){
            if(!getWorld().isAirBlock(getPos().offset(getFrontFacing())))
                return getWorld().getTileEntity(getPos().offset(getFrontFacing()));
        }
        return null;
    }

    public TileEntity getOutput(){
        if(!isInput() && isPaired()){
            if(!getWorld().isAirBlock(getPos().offset(getFrontFacing().getOpposite())))
                return getWorld().getTileEntity(getPos().offset(getFrontFacing().getOpposite()));
        }
        return null;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if(pair != null) pair.invalidatePair();
        invalidatePair();
    }

    @Override
    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing wrenchSide, CuboidRayTraceResult hitResult) {
        setFrontFacing(wrenchSide);
        return super.onWrenchClick(playerIn, hand, wrenchSide, hitResult);
    }

    public abstract boolean checkPipeType(BlockPipeline pipe, BlockPos location);

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
        Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        Textures.FLUID_HATCH_INPUT_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        Textures.PIPE_OUT_OVERLAY.renderSided(getFrontFacing().getOpposite(), renderState, translation, pipeline);
    }
}
