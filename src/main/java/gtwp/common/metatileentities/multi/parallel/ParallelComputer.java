package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IAddresable;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.impl.ParallelRecipeLogic;
import gtwp.api.gui.FrequencyGUI;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.ParallelAPI;
import gtwp.common.blocks.BlockCasing;
import gtwp.common.blocks.GTWPMetaBlocks;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import scala.collection.Parallel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static gregtech.api.util.RelativeDirection.*;

public class ParallelComputer extends MultiblockWithDisplayBase implements IAddresable {

    private CommunicationTower communicationTower;
    private int frequency = 0;
    private UUID netAddress;

    public ParallelComputer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

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
                .where('C', states(casingState()).or(autoAbilities(true, false)).or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(1)))
                .where('R', metaTileEntities(GTWPMetaTileEntities.PARALLEL_RACK))
                .where('T', abilities(GTWPMultiblockAbility.PARALLEL_HATCH_OUT))
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
        return GTWPMetaBlocks.CASING.getState(BlockCasing.Casings.COMPUTER);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTWPTextures.COMPUTER_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputer(metaTileEntityId);
    }

    @Override
    public void setNetAddress(UUID netAddress) {
        if(!getWorld().isRemote) {
            this.netAddress = netAddress;
            writeCustomData(GTWPDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
        }
    }

    @Override
    public void setNetAddress(EntityPlayer player, int frequency) {
        this.frequency = frequency;
        IAddresable.super.setNetAddress(player, frequency);
    }

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    public boolean isReceivingSignal() {
        if (isActive())
            return communicationTower != null && communicationTower.isReceivingSignal();
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
    public void update() {
        super.update();

        if(getWorld().isRemote) return;

        if(getOffsetTimer() % 20 == 0 && communicationTower == null)
            communicationTower = ParallelAPI.getActualTower(getNetAddress(), getPos());
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

    private boolean screwDriverClick;

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        screwDriverClick = true;
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI ui = screwDriverClick ? new FrequencyGUI(getHolder(), entityPlayer, frequency).createFrequencyUI() : super.createUI(entityPlayer);
        screwDriverClick = false;
        return ui;
    }
}
