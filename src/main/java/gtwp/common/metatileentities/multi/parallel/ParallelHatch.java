package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.common.items.GTWPMetaItems;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ParallelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IParallelHatch>, IParallelHatch {

    private final boolean transmitter;
    private ParallelHatch pair = null;
    private BlockPos pairPos;

    public ParallelHatch(ResourceLocation metaTileEntityId, boolean transmitter) {
        super(metaTileEntityId, 4);
        this.transmitter = transmitter;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelHatch(metaTileEntityId, transmitter);
    }

    public boolean setConnection(ParallelHatch pair){
        if( pair != null && pair.transmitter != transmitter) {
            pair.pair = this;
            pair.scheduleRenderUpdate();
            this.pair = pair;
            scheduleRenderUpdate();
        }
        return isConnected();
    }

    public boolean setConnection(BlockPos position){
        TileEntity te = getWorld().getTileEntity(position);
        if(te instanceof MetaTileEntityHolder){
            MetaTileEntityHolder mteh = ((MetaTileEntityHolder) te);
            if(mteh.getMetaTileEntity() instanceof ParallelHatch){
                return setConnection(((ParallelHatch) mteh.getMetaTileEntity()));
            }
        }
        return false;
    }

    public void breakConnection(){
        if(isConnected()) {
            this.pair.pair = null;
            this.pair.scheduleRenderUpdate();
            this.pair = null;
        }
        scheduleRenderUpdate();
    }

    public boolean isConnected(){
        return this.pair != null;
    }

    @Override
    public int getParallel(){
        scheduleRenderUpdate();
        if (transmitter) {
            if(((ParallelComputer) getController()).isReceivingSignal()) {
                TileEntity te = getWorld().getTileEntity(getPos().offset(getFrontFacing().getOpposite()));
                if (te instanceof MetaTileEntityHolder) {
                    MetaTileEntity mte = ((MetaTileEntityHolder) te).getMetaTileEntity();
                    if (mte instanceof ParallelComputerRack) {
                        return ((ParallelComputerRack) mte).getParallel();
                    }
                }
            }
        } else {
            if (isConnected())
                return pair.getParallel();
        }
        return 1;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        ItemStack item = playerIn.getHeldItem(hand);
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt != null) {
            if (playerIn.isSneaking()) {
                breakConnection();
                nbt.setInteger("inX", getPos().getX());
                nbt.setInteger("inY", getPos().getY());
                nbt.setInteger("inZ", getPos().getZ());
                GTWPChatUtils.sendMessage(playerIn, "Start connection");
            } else {
                int x = nbt.getInteger("inX");
                int y = nbt.getInteger("inY");
                int z = nbt.getInteger("inZ");
                BlockPos connectionPos = new BlockPos(x, y, z);
                if(getPos().equals(connectionPos)){
                    GTWPChatUtils.sendMessage(playerIn, "Cannot connect to self");
                }else {
                    if(setConnection(connectionPos)) {
                        GTWPChatUtils.sendMessage(playerIn, "Connection successful " + x + " " + y + " " + z);
                        nbt.removeTag("inX");
                        nbt.removeTag("inY");
                        nbt.removeTag("inZ");
                    } else GTWPChatUtils.sendMessage(playerIn,
                            "Cannot connect "+(transmitter?"transmitter to transmitter":"receiver to receiver"));
                }
            }
        }
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        if(!isConnected()) return GTWPTextures.PARALLEL_HATCH_RED;
        return (transmitter ? this.pair : this).getController().isActive() ? GTWPTextures.PARALLEL_HATCH_GREEN : GTWPTextures.PARALLEL_HATCH_YELLOW;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getHatchOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public MultiblockAbility<IParallelHatch> getAbility() {
        return transmitter ? null : GTWPMultiblockAbility.PARALLEL_HATCH;
    }

    @Override
    public void registerAbilities(List<IParallelHatch> list) {
        if(!transmitter) list.add(this);
    }
}
