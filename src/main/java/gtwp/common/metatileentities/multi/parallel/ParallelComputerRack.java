package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.render.GTWPTextures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ParallelComputerRack extends MetaTileEntityMultiblockPart {

    private IItemHandlerModifiable inventory;

    public ParallelComputerRack(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
        initializeInventory();
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

    private boolean isHatchActive(){
        BlockPos parallelHatch = getPos().offset(getFrontFacing().getOpposite());
        TileEntity te = getWorld().getTileEntity(parallelHatch);
        if(te instanceof MetaTileEntityHolder){
            if(((MetaTileEntityHolder) te).getMetaTileEntity() instanceof ParallelHatch){
                return ((ParallelHatch) ((MetaTileEntityHolder) te).getMetaTileEntity()).isConnected();
            }
        }
        return false;
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        return GTWPTextures.COMPUTER_CASING;
    }

    public int getParallel(){
        return 64;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(GuiTextures.BACKGROUND, 180, 120)
                .widget(new SlotWidget(itemInventory, 0, 10, 10, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 1, 28, 10, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 2, 10, 28, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 3, 28, 28, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .label(10, 5, getMetaFullName())
                .build(getHolder(), entityPlayer);
    }
}
