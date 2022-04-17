package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.api.utils.PrecisionParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
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
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SatelliteHatch(metaTileEntityId, transmitter);
    }

    public boolean isTransmitting() {
        return transmitter && getController() != null && getController().isActive() && ((Satellite) getController()).isAbleToWork();
    }

    public boolean setConnection(UUID netAddress) {
        if(transmitter) return false;
        SatelliteHatch newPair = PrecisionParallelAPI.getTransmitterByNetAddress(netAddress);
        if (newPair != pair) {
            pair = newPair;
            if(pair != null)
                pair.scheduleRenderUpdate();
            scheduleRenderUpdate();
        }
        return isReceivingSignal();
    }

    public SatelliteHatch getConnection(){
        return pair;
    }

    @Override
    public boolean isReceivingSignal() {
        return transmitter || pair != null;
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
    public void setNetAddress(UUID newNetAddress) {
        if(!getWorld().isRemote) {
            if (transmitter) {
                PrecisionParallelAPI.removeSatelliteTransmitter(netAddress);
                this.netAddress = newNetAddress;
                PrecisionParallelAPI.addSatelliteTransmitter(netAddress, this);
            } else this.netAddress = newNetAddress;
            writeCustomData(PrecisionDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
        }
    }

    @Override
    public void setNetAddress(EntityPlayer player, int frequency) {
        if(!getWorld().isRemote) {
            this.frequency = frequency;
            IAddresable.super.setNetAddress(player, frequency);
        }
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        if(transmitter)
            return getController() == null ? PrecisionTextures.PARALLEL_HATCH_RED : getController() != null && getController().isActive() ? PrecisionTextures.PARALLEL_HATCH_GREEN : PrecisionTextures.PARALLEL_HATCH_YELLOW;
        else return (isReceivingSignal() ? (getConnection().isTransmitting() ? PrecisionTextures.PARALLEL_HATCH_GREEN : PrecisionTextures.PARALLEL_HATCH_YELLOW) : PrecisionTextures.PARALLEL_HATCH_RED);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getHatchOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
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
        return transmitter ? PrecisionMultiblockAbility.SATELLITE_TRANSMITTER : PrecisionMultiblockAbility.SATELLITE_RECEIVER;
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
            writeCustomData(PrecisionDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        frequency = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.NET_ADDRESS_UPDATE)
            netAddress = buf.readUniqueId();
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if(transmitter)
            PrecisionParallelAPI.removeSatelliteTransmitter(netAddress);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if(transmitter)
            PrecisionParallelAPI.removeSatelliteTransmitter(netAddress);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if(transmitter)
            PrecisionParallelAPI.addSatelliteTransmitter(netAddress, this);
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        T result = super.getCapability(capability, side);
        if (result != null)
            return result;
        if (capability == PrecisionCapabilities.CAPABILITY_ADDRESSABLE) {
            return PrecisionCapabilities.CAPABILITY_ADDRESSABLE.cast(this);
        }
        return null;
    }
}
