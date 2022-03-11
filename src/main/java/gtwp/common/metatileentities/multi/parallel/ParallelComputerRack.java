package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.VariantItemBlock;
import gregtech.common.items.behaviors.TurbineRotorBehavior;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.render.GTWPTextures;
import gtwp.common.blocks.BlockParallel;
import gtwp.common.blocks.GTWPMetaBlocks;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import static net.minecraft.block.Block.getBlockFromItem;

public class ParallelComputerRack extends MetaTileEntityMultiblockPart {

    private RackInventoryHolder inventory;

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
        GTWPTextures.PARALLEL_RACK.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        return GTWPTextures.COMPUTER_CASING;
    }

    public int getParallel() {
        return 64;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(GuiTextures.BACKGROUND, 180, 180)
                .label(10, 5, getMetaFullName())
                .slot(inventory, 0, 40, 40, GuiTextures.SLOT)
                .slot(inventory, 1, 86, 40, GuiTextures.SLOT)
                .slot(inventory, 2, 40, 86, GuiTextures.SLOT)
                .slot(inventory, 3, 86, 86, GuiTextures.SLOT)
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
            if(getBlockFromItem(stack.getItem()) instanceof BlockParallel)
                return super.isItemValid(slot, stack);
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            scheduleRenderUpdate();
        }
    }
}