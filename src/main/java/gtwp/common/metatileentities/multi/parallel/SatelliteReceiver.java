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
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IReceiver;
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

public class SatelliteReceiver extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IReceiver>, IReceiver {

    private int frequency = 0;
    private UUID netAddress;
    private SatelliteTransmitter pair = null;

    public SatelliteReceiver(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new SatelliteReceiver(metaTileEntityId);
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
            if (facing == getFrontFacing()) {
                if (playerIn.isSneaking()) frequency--;
                else frequency++;
                netAddress = generateNetAddress(playerIn, frequency);
                GTWPChatUtils.sendMessage(playerIn, "Receiver frequency: " + frequency);
                GTWPChatUtils.sendMessage(playerIn, "UUID: " + netAddress);
            }
        }
        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
    }

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    @Override
    public boolean setConnection(UUID netAddress) {
        if(!getWorld().isRemote) {
            SatelliteTransmitter newPair = ParallelAPI.getTransmitterByNetAddress(netAddress);
            if (newPair != pair) {
                pair = newPair;
                if (pair != null)
                    pair.writeCustomData(GTWPDataCodes.RENDER_PARALLEL_HATCH, b -> b.writeBoolean(true));
                writeCustomData(GTWPDataCodes.RENDER_PARALLEL_HATCH, b -> b.writeBoolean(true));
            }
        }
        return isConnected();
    }

    public SatelliteTransmitter getConnection(){
        return pair;
    }

    @Override
    public boolean isConnected() {
        return pair != null;
    }

    @Override
    public void update() {
        super.update();
        if (getOffsetTimer() % 20 == 0)
            setConnection(netAddress);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        (isConnected() ? (getConnection().isTransmitting() ? GTWPTextures.PARALLEL_HATCH_GREEN : GTWPTextures.PARALLEL_HATCH_YELLOW) : GTWPTextures.PARALLEL_HATCH_RED).renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public MultiblockAbility<IReceiver> getAbility() {
        return GTWPMultiblockAbility.RECEIVER;
    }

    @Override
    public void registerAbilities(List<IReceiver> list) {
        list.add(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("frequency", frequency);
        data.setUniqueId("netAddress", netAddress);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        frequency = data.getInteger("frequency");
        netAddress = data.getUniqueId("netAddress");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.frequency);
        buf.writeBoolean(netAddress != null);
        if(netAddress != null)
            buf.writeUniqueId(netAddress);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.frequency = buf.readInt();
        if(buf.readBoolean())
            netAddress = buf.readUniqueId();
        else netAddress = null;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.RENDER_PARALLEL_HATCH){
            if(buf.readBoolean()) scheduleRenderUpdate();
        }
    }
}
