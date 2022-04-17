package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.mojang.realmsclient.gui.ChatFormatting;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.capability.IParallelHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.api.utils.PrecisionChatUtils;
import precisioncore.api.utils.PrecisionUtility;
import precisioncore.api.utils.PrecisionParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class ParallelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IParallelHatch>, IParallelHatch {

    private final boolean transmitter;
    private BlockPos pairPos;
    private final int tierParallel;
    private byte renderState = 2;
    private UUID address;

    public ParallelHatch(ResourceLocation metaTileEntityId, int tier, boolean transmitter) {
        super(metaTileEntityId, tier); //tier starts from 5
        this.transmitter = transmitter;
        this.tierParallel = (int)Math.pow(4, tier-4);
        this.address = transmitter ? UUID.randomUUID() : null;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new ParallelHatch(metaTileEntityId, getTier(), transmitter);
    }

    public UUID getAddress(){
        return this.address;
    }

    public boolean isTransmitter() {
        return this.transmitter;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        T result = super.getCapability(capability, side);
        if (result != null)
            return result;
        if (capability == PrecisionCapabilities.CAPABILITY_PARALLEL) {
            return PrecisionCapabilities.CAPABILITY_PARALLEL.cast(this);
        }
        return null;
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
                            if(!transmitter)
                                address = pair.address;
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
            if(!transmitter)
                address = null;
            if(pairPos != null){
                if(goBrrrtRecursion && getPair() != null)
                    getPair().breakConnection(false);
                pairPos = null;
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
                if (isConnected() && PrecisionParallelAPI.netAddressEqual(((IAddresable) getPair().getController()).getNetAddress(), ((IAddresable) getController()).getNetAddress()))
                    return getPair().getParallel();
            }
        }
        return 1;
    }

    private byte updateRenderState(){
        byte newRenderState = 2;
        if(!getWorld().isRemote) {
            if (isConnected())
                newRenderState = (byte) ((transmitter ? getPair() : this).getController() != null && (transmitter ? getPair() : this).getController().isActive() ? 0 : 1);
        }
        return newRenderState;
    }

    @Override
    public void update() {
        super.update();

        if(getWorld().isRemote) return;

        if(getOffsetTimer() % 8 == 0) {
            byte newRenderState = updateRenderState();
            if (newRenderState != renderState) {
                renderState = newRenderState;
                writeCustomData(PrecisionDataCodes.RENDER_STATE_UPDATE, b -> b.writeByte(renderState));
            }
        }
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
        if(!getWorld().isRemote) {
            ItemStack item = playerIn.getHeldItem(hand);
            NBTTagCompound nbt = item.getTagCompound();
            if (nbt != null) {
                if (playerIn.isSneaking()) {
                    breakConnection();
                    nbt.setIntArray("inPos", PrecisionUtility.BlockPosToInt3(getPos()));
                    PrecisionChatUtils.sendMessage(playerIn, ChatFormatting.YELLOW+"Start connection");
                } else {
                    if (nbt.hasKey("inPos")) {
                        BlockPos connectionPos = PrecisionUtility.Int3ToBlockPos(nbt.getIntArray("inPos"));
                        if (setConnection(connectionPos)) {
                            PrecisionChatUtils.sendMessage(playerIn, ChatFormatting.GREEN+"Connection successful");
                            PrecisionChatUtils.sendMessage(playerIn, "Current parallel: "+ChatFormatting.DARK_GREEN+getParallel());
                            nbt.removeTag("inPos");
                        } else PrecisionChatUtils.sendMessage(playerIn, ChatFormatting.RED+"Connection failed");
                    }
                }
            }
        }
        return super.onLaptopClick(playerIn, hand, facing, hitResult);
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        switch (renderState){
            case 0:
                return PrecisionTextures.PARALLEL_HATCH_GREEN;
            case 1:
                return PrecisionTextures.PARALLEL_HATCH_YELLOW;
            default:
                return PrecisionTextures.PARALLEL_HATCH_RED;
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getHatchOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public MultiblockAbility<IParallelHatch> getAbility() {
        return transmitter ? PrecisionMultiblockAbility.PARALLEL_HATCH_OUT : PrecisionMultiblockAbility.PARALLEL_HATCH_IN;
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
            data.setIntArray("pairPos", PrecisionUtility.BlockPosToInt3(pairPos));
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        renderState = data.getByte("renderState");
        if(data.hasKey("pairPos"))
            pairPos = PrecisionUtility.Int3ToBlockPos(data.getIntArray("pairPos"));
        else pairPos = null;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        writeCustomData(PrecisionDataCodes.RENDER_STATE_UPDATE, b -> b.writeByte(renderState));
        if(isConnected())
            writeCustomData(PrecisionDataCodes.RECEIVE_PAIR_POS, b -> b.writeBlockPos(pairPos).writeBoolean(true));
        else writeCustomData(PrecisionDataCodes.RECEIVE_PAIR_POS, b -> b.writeBoolean(false));
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.RECEIVE_PAIR_POS){
            if(buf.readBoolean()) pairPos = buf.readBlockPos();
            else pairPos = null;
        } else if(dataId == PrecisionDataCodes.RENDER_STATE_UPDATE){
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
