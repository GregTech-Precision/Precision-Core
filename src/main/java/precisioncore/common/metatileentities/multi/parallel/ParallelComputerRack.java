package precisioncore.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.blocks.VariantItemBlock;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import precisioncore.api.gui.PrecisionGUITextures;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasingParallel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import java.io.IOException;

import static net.minecraft.block.Block.getBlockFromItem;

public class ParallelComputerRack extends MetaTileEntityMultiblockPart {

    private final RackInventoryHolder inventory;
    private int parallelPoints = 0;

    public ParallelComputerRack(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5);
        inventory = new RackInventoryHolder();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new ParallelComputerRack(metaTileEntityId);
    }

    private SimpleOverlayRenderer getRackOverlay(){
        if(getParallelPoints() > 0) {
            if (getController() != null && ((ParallelComputer) getController()).isActive()) return PrecisionTextures.PARALLEL_RACK_ACTIVE;
            else return  PrecisionTextures.PARALLEL_RACK_INACTIVE;
        }else return PrecisionTextures.PARALLEL_RACK_EMPTY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getRackOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    public int getParallelPoints() {
        return parallelPoints;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(PrecisionGUITextures.PARALLEL_RACK_GUI, 176, 166)
                .label(10, 5, getMetaFullName())
                .slot(inventory, 0, 68, 27, GuiTextures.SLOT, PrecisionGUITextures.RACK_SLOT)
                .slot(inventory, 1, 90, 27, GuiTextures.SLOT, PrecisionGUITextures.RACK_SLOT)
                .slot(inventory, 2, 68, 49, GuiTextures.SLOT, PrecisionGUITextures.RACK_SLOT)
                .slot(inventory, 3, 90, 49, GuiTextures.SLOT, PrecisionGUITextures.RACK_SLOT)
                .bindPlayerInventory(entityPlayer.inventory)
                .build(getHolder(), entityPlayer);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("inventory", this.inventory.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.inventory.deserializeNBT(data.getCompoundTag("inventory"));
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeCompoundTag(inventory.serializeNBT());
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        try {
            inventory.deserializeNBT(buf.readCompoundTag());
        } catch (IOException e) {
            GTLog.logger.error("Null initial sync data inventory compound tag");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        parallelPoints = inventory.countParallelPoints();
        scheduleRenderUpdate();
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        parallelPoints = inventory.countParallelPoints();
        scheduleRenderUpdate();
    }

    private class RackInventoryHolder extends ItemStackHandler {

        RackInventoryHolder() {
            super(4);
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return getBlockFromItem(stack.getItem()) instanceof BlockCasingParallel && super.isItemValid(slot, stack);
        }

        public int countParallelPoints(){
            int parallelPoints = 0;
            for(int i = 0; i<getSlots();i++){
                ItemStack item = getStackInSlot(i);
                if(!item.isEmpty())
                    parallelPoints += ((BlockCasingParallel) ((VariantItemBlock) item.getItem()).getBlock()).getState(item).getParallelPoints();
            }
            return parallelPoints;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            parallelPoints = countParallelPoints();
            scheduleRenderUpdate();
        }
    }
}