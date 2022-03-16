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
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.render.GTWPTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.GTWPUtility;
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
import java.util.function.Consumer;

public class ParallelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IParallelHatch>, IParallelHatch {

    private final boolean transmitter;
    private ParallelHatch pair = null;

    public ParallelHatch(ResourceLocation metaTileEntityId, boolean transmitter) {
        super(metaTileEntityId, 5);
        this.transmitter = transmitter;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelHatch(metaTileEntityId, transmitter);
    }

    public boolean setConnection(ParallelHatch pair){
        if( pair != null && pair.transmitter != transmitter) {
            breakConnection();
            pair.pair = this;
            pair.scheduleRenderUpdate();
            this.pair = pair;
            scheduleRenderUpdate();
            writeCustomData(GTWPDataCodes.RECEIVE_PAIR_POS, b -> b.writeBlockPos(pair.getPos()));
        }
        return isConnected();
    }

    public boolean setConnection(BlockPos position){
        if(getWorld().isBlockLoaded(position)) {
            TileEntity te = getWorld().getTileEntity(position);
            if (te instanceof MetaTileEntityHolder) {
                MetaTileEntityHolder mteh = ((MetaTileEntityHolder) te);
                if (mteh.getMetaTileEntity() instanceof ParallelHatch) {
                    return setConnection(((ParallelHatch) mteh.getMetaTileEntity()));
                }
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
                        return ((ParallelComputerRack) mte).getParallelPoints();
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
    protected boolean openGUIOnRightClick() {
        return false;
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
                    } else GTWPChatUtils.sendMessage(playerIn, "Connection failed");
                }
            }
        }
        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
    }

    private SimpleOverlayRenderer getHatchOverlay(){
        if(!isConnected() || (transmitter ? this.pair : this).getController() == null) return GTWPTextures.PARALLEL_HATCH_RED;
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

    private BlockPos pairPos;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if(isConnected())
            data.setIntArray("pairPos", GTWPUtility.BlockPosToInt3(pair.getPos()));
        else if(pairPos != null)
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
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.RECEIVE_PAIR_POS)
            pairPos = buf.readBlockPos();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if(pair != null)
            pairPos = pair.getPos();
        breakConnection();
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        breakConnection();
    }

    @Override
    public void update() {
        super.update();
        if(pairPos != null && getOffsetTimer() % 20 == 0 && setConnection(pairPos))
            pairPos = null;
    }
}
