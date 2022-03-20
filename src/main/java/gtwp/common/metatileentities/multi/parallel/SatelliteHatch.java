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
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IAddresable;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.ParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class SatelliteHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IAddresable>, IAddresable {

    private final boolean transmitter;

    private int frequency = 0;
    private UUID netAddress;
    private SatelliteHatch pair;

    public SatelliteHatch(ResourceLocation metaTileEntityId, boolean transmitter) {
        super(metaTileEntityId, 5);
        this.transmitter = transmitter;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new SatelliteHatch(metaTileEntityId, transmitter);
    }

    public boolean isTransmitting() {
        return transmitter && getController() != null && getController().isActive() && ((Satellite) getController()).isAbleToWork();
    }

    public boolean setConnection(UUID netAddress) {
        if(transmitter) return false;
        SatelliteHatch newPair = ParallelAPI.getTransmitterByNetAddress(netAddress);
        if (newPair != pair) {
            pair = newPair;
            if(pair != null)
                pair.scheduleRenderUpdate();
            scheduleRenderUpdate();
        }
        return isConnected();
    }

    public SatelliteHatch getConnection(){
        return pair;
    }

    public boolean isConnected() {
        return !transmitter && pair != null;
    }

    @Override
    public void update() {
        super.update();
        if(transmitter) return;
        if (getOffsetTimer() % 20 == 0)
            setConnection(netAddress);
    }

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    @Override
    public void setNetAddress(UUID netAddress) {
        this.netAddress = netAddress;
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        if(transmitter)
            return getController() == null ? GTWPTextures.PARALLEL_HATCH_RED : getController() != null && getController().isActive() ? GTWPTextures.PARALLEL_HATCH_GREEN : GTWPTextures.PARALLEL_HATCH_YELLOW;
        else return (isConnected() ? (getConnection().isTransmitting() ? GTWPTextures.PARALLEL_HATCH_GREEN : GTWPTextures.PARALLEL_HATCH_YELLOW) : GTWPTextures.PARALLEL_HATCH_RED);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getHatchOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if(!getWorld().isRemote) {
            if (facing == getFrontFacing()) {
                if (playerIn.isSneaking()) frequency--;
                else frequency++;

                if(transmitter) {
                    ParallelAPI.removeSatelliteTransmitter(netAddress);
                    setNetAddress(playerIn, frequency);
                    ParallelAPI.addSatelliteTransmitter(netAddress, this);
                } else netAddress = generateNetAddress(playerIn, frequency);

                writeCustomData(GTWPDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));

                GTWPChatUtils.sendMessage(playerIn, "Frequency: " + frequency);
                GTWPChatUtils.sendMessage(playerIn, "UUID: " + netAddress);
            }
        }
        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
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
    public MultiblockAbility<IAddresable> getAbility() {
        return transmitter ? GTWPMultiblockAbility.SATELLITE_TRANSMITTER : GTWPMultiblockAbility.SATELLITE_RECEIVER;
    }

    @Override
    public void registerAbilities(List<IAddresable> list) {
        list.add(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("frequency", frequency);
        if(netAddress != null)
            data.setUniqueId("netAddress", netAddress);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        frequency = data.getInteger("frequency");
        if(data.hasUniqueId("netAddress"))
            netAddress = data.getUniqueId("netAddress");
        else netAddress = null;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(frequency);
        if(netAddress != null)
            writeCustomData(GTWPDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        frequency = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.NET_ADDRESS_UPDATE)
            netAddress = buf.readUniqueId();
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if(transmitter)
            ParallelAPI.removeSatelliteTransmitter(netAddress);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if(transmitter)
            ParallelAPI.removeSatelliteTransmitter(netAddress);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(transmitter)
            ParallelAPI.addSatelliteTransmitter(netAddress, this);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if(transmitter)
            ParallelAPI.addSatelliteTransmitter(netAddress, this);
    }
}
