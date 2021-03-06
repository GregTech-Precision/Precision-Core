package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.utils.PrecisionParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

public class CommunicationTower extends MultiblockWithDisplayBase implements IAddresable {

    private UUID netAddress;
    private int frequency = 0;

    public CommunicationTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new CommunicationTower(metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentString("Satellite connection: " + (isReceivingSignal() ? "online" : "offline")));
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("###", "###", "###", "###", "###", "###", "###", "###", "###", "#R#")
                .aisle("#F#", "#S#", "#F#", "#F#", "#F#", "#F#", "#F#", "#F#", "#F#", "RFR")
                .aisle("###", "###", "###", "###", "###", "###", "###", "###", "###", "#R#")
                .where('S', selfPredicate())
                .where('R', abilities(PrecisionMultiblockAbility.SATELLITE_RECEIVER))
                .where('F', states(MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel)))
                .where('#', any())
                .build();
    }

    public boolean isReceivingSignal(){
        if(!getWorld().isRemote){
            if(isActive()) {
                List<IAddresable> receiverList = getAbilities(PrecisionMultiblockAbility.SATELLITE_RECEIVER);
                if (!receiverList.isEmpty()) {
                    for (IAddresable receiver : receiverList) {
                        if (receiver instanceof SatelliteHatch) {
                            if (!(((SatelliteHatch) receiver).isReceivingSignal() && ((SatelliteHatch) receiver).getConnection().isTransmitting())) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setNetAddress(UUID newNetAddress) {
        if(!getWorld().isRemote) {
            PrecisionParallelAPI.removeCommunicationTower(netAddress, this);
            netAddress = newNetAddress;
            PrecisionParallelAPI.addCommunicationTower(netAddress, this);
            writeCustomData(PrecisionDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
        }
    }

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    @Override
    public void setNetAddress(EntityPlayer player, int frequency) {
        if(!getWorld().isRemote) {
            this.frequency = frequency;
            IAddresable.super.setNetAddress(player, frequency);
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
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
    public void onUnload() {
        super.onUnload();
        PrecisionParallelAPI.removeCommunicationTower(getNetAddress(), this);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        PrecisionParallelAPI.removeCommunicationTower(getNetAddress(), this);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        PrecisionParallelAPI.addCommunicationTower(getNetAddress(), this);
    }

    @Override
    public boolean onLaptopClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        return true;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        T result = super.getCapability(capability, side);
        if(result != null)
            return result;

        if(capability == PrecisionCapabilities.CAPABILITY_ADDRESSABLE)
            return PrecisionCapabilities.CAPABILITY_ADDRESSABLE.cast(this);
        return null;
    }
}
