package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.capability.impl.ParallelComputerLogic;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.api.utils.PrecisionParallelAPI;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;
import precisioncore.common.metatileentities.PrecisionMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static gregtech.api.util.RelativeDirection.*;

public class ParallelComputer extends MultiblockWithDisplayBase implements IAddresable {

    private int frequency = 0;
    private UUID netAddress;
    private final ParallelComputerLogic workableLogic;

    public ParallelComputer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.workableLogic = new ParallelComputerLogic(this);
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentString("Parallel points: "+getCurrentParallelPoints()));
        textList.add(new TextComponentString("Satellite connection: " + (isReceivingSignal() ? "online" : "offline")));
        super.addDisplayText(textList);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(LEFT, UP, FRONT)
                .aisle("CC", "CS", "CC", "CC")
                .aisle("CC", "RT", "RT", "CC").setRepeatable(2, 16)
                .aisle("CC", "CC", "CC", "CC")
                .where('S', selfPredicate())
                .where('C', states(casingState()).or(autoAbilities(true, false)).or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(4)))
                .where('R', metaTileEntities(PrecisionMetaTileEntities.PARALLEL_RACK))
                .where('T', abilities(PrecisionMultiblockAbility.PARALLEL_HATCH_OUT))
                .build();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    public int getCurrentParallelPoints(){
        int parallelPoints = 0;
        for(IMultiblockPart imp : getMultiblockParts())
            if(imp instanceof ParallelComputerRack)
                parallelPoints += ((ParallelComputerRack) imp).getParallelPoints();
        return parallelPoints;
    }

    private IBlockState casingState(){
        return PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.COMPUTER);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.COMPUTER_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new ParallelComputer(metaTileEntityId);
    }

    @Override
    public void setNetAddress(UUID newNetAddress) {
        if(!getWorld().isRemote) {
            this.netAddress = newNetAddress;
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

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    public boolean isReceivingSignal() {
        if (workableLogic.isWorking())
            return PrecisionParallelAPI.getActualTower(getNetAddress(), getPos()) != null && PrecisionParallelAPI.getActualTower(getNetAddress(), getPos()).isReceivingSignal();
        return false;
    }

    @Nonnull
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
