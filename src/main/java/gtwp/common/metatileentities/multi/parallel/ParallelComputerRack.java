package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.VariantItemBlock;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.gui.GTWPGuiTextures;
import gtwp.api.render.GTWPTextures;
import gtwp.common.blocks.BlockCasingParallel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import static net.minecraft.block.Block.getBlockFromItem;

public class ParallelComputerRack extends MetaTileEntityMultiblockPart {

    private RackInventoryHolder inventory;
    private int parallelPoints = 0;

    public ParallelComputerRack(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
        inventory = new RackInventoryHolder();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputerRack(metaTileEntityId);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        (parallelPoints > 1 ? GTWPTextures.PARALLEL_RACK_ACTIVE : GTWPTextures.PARALLEL_RACK).renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        return GTWPTextures.COMPUTER_CASING;
    }

    public int getParallel() {
        return parallelPoints;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(GTWPGuiTextures.PARALLEL_RACK, 176, 166)
                .label(10, 5, getMetaFullName())
                .slot(inventory, 0, 68, 27, GuiTextures.SLOT)
                .slot(inventory, 1, 90, 27, GuiTextures.SLOT)
                .slot(inventory, 2, 68, 49, GuiTextures.SLOT)
                .slot(inventory, 3, 90, 49, GuiTextures.SLOT)
                .bindPlayerInventory(entityPlayer.inventory)
                .build(getHolder(), entityPlayer);
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
            return Math.max(1, parallelPoints);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            parallelPoints = countParallelPoints();
            scheduleRenderUpdate();
        }
    }
}