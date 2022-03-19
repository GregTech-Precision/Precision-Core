package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IAddresable;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.GTWPUtility;
import gtwp.api.utils.ParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.collection.Parallel;

import java.util.List;
import java.util.function.Consumer;

public class ParallelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IParallelHatch>, IParallelHatch {

    private final boolean transmitter;
    private BlockPos pairPos;
    private final int tierParallel;

    public ParallelHatch(ResourceLocation metaTileEntityId, int tier, boolean transmitter) {
        super(metaTileEntityId, tier); //tier starts from 5
        this.transmitter = transmitter;
        this.tierParallel = (int)Math.pow(4, tier-4);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelHatch(metaTileEntityId, getTier(), transmitter);
    }

    public boolean setConnection(BlockPos position) {
        if(!getWorld().isRemote) {
            if (getWorld().isBlockLoaded(position)) {
                TileEntity te = getWorld().getTileEntity(position);
                if (te instanceof MetaTileEntityHolder) {
                    if (((MetaTileEntityHolder) te).getMetaTileEntity() instanceof ParallelHatch) {
                        if (((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity()).transmitter != transmitter && ((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity()).getTier() == getTier()) {
                            breakConnection();
                            pairPos = position;
                            ParallelHatch pair = getPair();
                            pair.breakConnection();
                            pair.pairPos = getPos();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ParallelHatch getPair() {
        if(!getWorld().isRemote){
            if(getWorld().isBlockLoaded(pairPos)){
                TileEntity te = getWorld().getTileEntity(pairPos);
                if(te instanceof MetaTileEntityHolder){
                    if(((MetaTileEntityHolder) te).getMetaTileEntity() instanceof ParallelHatch){
                        if(((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity()).transmitter != transmitter && ((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity()).getTier() == getTier()){
                            return ((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity());
                        }
                    } else pairPos = null;
                } else pairPos = null;
            }
        }
        return null;
    }

    public void breakConnection(){
        if(!getWorld().isRemote){
            if(isConnected()){
                ParallelHatch pair = getPair();
                if(pair != null) {
                    pair.pairPos = null;
                }
            }
        }
    }

    public boolean isConnected(){
        return pairPos != null && getPair() != null;
    }

    @Override
    public int getParallel(){
        if(!getWorld().isRemote) {
            if (transmitter) {
                if (((ParallelComputer) getController()).isReceivingSignal()) {
                    TileEntity te = getWorld().getTileEntity(getPos().offset(getFrontFacing().getOpposite()));
                    if (te instanceof MetaTileEntityHolder) {
                        MetaTileEntity mte = ((MetaTileEntityHolder) te).getMetaTileEntity();
                        if (mte instanceof ParallelComputerRack) {
                            return Math.min(tierParallel, ((ParallelComputerRack) mte).getParallelPoints());
                        }
                    }
                }
            } else {
                if (isConnected() && ParallelAPI.netAddressEqual(((IAddresable) getPair().getController()).getNetAddress(), ((IAddresable) getController()).getNetAddress()))
                    return getPair().getParallel();
            }
        }
        return 1;
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
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if(!getWorld().isRemote) {
            ItemStack item = playerIn.getHeldItem(hand);
            NBTTagCompound nbt = item.getTagCompound();
            if (nbt != null) {
                if (playerIn.isSneaking()) {
                    breakConnection();
                    nbt.setIntArray("inPos", GTWPUtility.BlockPosToInt3(getPos()));
                    GTWPChatUtils.sendMessage(playerIn, "Start connection");
                } else {
                    if (nbt.hasKey("inPos")) {
                        BlockPos connectionPos = GTWPUtility.Int3ToBlockPos(nbt.getIntArray("inPos"));
                        if (setConnection(connectionPos)) {
                            GTWPChatUtils.sendMessage(playerIn, "Connection successful");
                            nbt.removeTag("inPos");
                        } else GTWPChatUtils.sendMessage(playerIn, "Connection failed");
                    }
                }
            }
        }
        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        if(isConnected()){
            ParallelHatch ph = transmitter ? getPair() : this;
            return ph.getController() != null && ph.getController().isActive() ? GTWPTextures.PARALLEL_HATCH_GREEN : GTWPTextures.PARALLEL_HATCH_YELLOW;
        }
        return GTWPTextures.PARALLEL_HATCH_RED;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getHatchOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public MultiblockAbility<IParallelHatch> getAbility() {
        return transmitter ? GTWPMultiblockAbility.PARALLEL_HATCH_OUT : GTWPMultiblockAbility.PARALLEL_HATCH_IN;
    }

    @Override
    public void registerAbilities(List<IParallelHatch> list) {
        list.add(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if(isConnected())
            data.setIntArray("pairPos", GTWPUtility.BlockPosToInt3(pairPos));
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if(data.hasKey("pairPos"))
            pairPos = GTWPUtility.Int3ToBlockPos(data.getIntArray("pairPos"));
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(isConnected());
        if(isConnected())
            buf.writeBlockPos(pairPos);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        if(buf.readBoolean())
            pairPos = buf.readBlockPos();
        else pairPos = null;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        breakConnection();
    }
}
