package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.mojang.realmsclient.gui.ChatFormatting;
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
    private byte renderState = 2;

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
                            pair.updateRenderState();
                            return true;
                        }
                    }
                }
            }
            updateRenderState();
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
                    } else breakConnection(false);
                } else breakConnection(false);
            }
        }
        return null;
    }

    public void breakConnection(){
        breakConnection(true);
    }

    private void breakConnection(boolean goBrrrtRecursion){
        if(!getWorld().isRemote){
            if(pairPos != null){
                if(goBrrrtRecursion && getPair() != null)
                    getPair().breakConnection(false);
                pairPos = null;
            }
        }
        updateRenderState();
    }

    public boolean isConnected(){
        return pairPos != null && getPair() != null;
    }

    @Override
    public int getParallel(){
        if(!getWorld().isRemote) {
            updateRenderState();
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

    private byte updateRenderState(){
        byte newRenderState = 2;
        if(!getWorld().isRemote) {
            if (isConnected()) {
                renderState = (byte) ((transmitter ? getPair() : this).getController() != null && (transmitter ? getPair() : this).getController().isActive() ? 0 : 1);
            }
        }
        return newRenderState;
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
    public boolean onLaptopClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        GTWPChatUtils.sendMessage(playerIn, "laptop click");
        if(!getWorld().isRemote) {
            ItemStack item = playerIn.getHeldItem(hand);
            NBTTagCompound nbt = item.getTagCompound();
            if (nbt != null) {
                if (playerIn.isSneaking()) {
                    breakConnection();
                    nbt.setIntArray("inPos", GTWPUtility.BlockPosToInt3(getPos()));
                    GTWPChatUtils.sendMessage(playerIn, ChatFormatting.YELLOW+"Start connection");
                } else {
                    if (nbt.hasKey("inPos")) {
                        BlockPos connectionPos = GTWPUtility.Int3ToBlockPos(nbt.getIntArray("inPos"));
                        if (setConnection(connectionPos)) {
                            GTWPChatUtils.sendMessage(playerIn, ChatFormatting.GREEN+"Connection successful");
                            GTWPChatUtils.sendMessage(playerIn, "Current parallel: "+ChatFormatting.DARK_GREEN+getParallel());
                            nbt.removeTag("inPos");
                        } else GTWPChatUtils.sendMessage(playerIn, ChatFormatting.RED+"Connection failed");
                    }
                }
            } else GTWPChatUtils.sendMessage(playerIn, "null nbt");
        }
        return super.onLaptopClick(playerIn, hand, facing, hitResult);
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        switch (renderState){
            case 0:
                return GTWPTextures.PARALLEL_HATCH_GREEN;
            case 1:
                return GTWPTextures.PARALLEL_HATCH_YELLOW;
            default:
                return GTWPTextures.PARALLEL_HATCH_RED;
        }
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
        data.setByte("renderState", renderState);
        if(isConnected())
            data.setIntArray("pairPos", GTWPUtility.BlockPosToInt3(pairPos));
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        renderState = data.getByte("renderState");
        if(data.hasKey("pairPos"))
            pairPos = GTWPUtility.Int3ToBlockPos(data.getIntArray("pairPos"));
        else pairPos = null;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        writeCustomData(GTWPDataCodes.RENDER_STATE_UPDATE, b -> b.writeByte(renderState));
        if(isConnected())
            writeCustomData(GTWPDataCodes.RECEIVE_PAIR_POS, b -> b.writeBlockPos(pairPos).writeBoolean(true));
        else writeCustomData(GTWPDataCodes.RECEIVE_PAIR_POS, b -> b.writeBoolean(false));
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.RECEIVE_PAIR_POS){
            if(buf.readBoolean()) pairPos = buf.readBlockPos();
            else pairPos = null;
        } else if(dataId == GTWPDataCodes.RENDER_STATE_UPDATE){
            renderState = buf.readByte();
            scheduleRenderUpdate();
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        breakConnection();
    }
}
