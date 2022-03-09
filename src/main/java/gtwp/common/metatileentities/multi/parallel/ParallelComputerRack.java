package gtwp.common.metatileentities.multi.parallel;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

public class ParallelComputerRack extends MetaTileEntityMultiblockPart {

    public ParallelComputerRack(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelComputerRack(metaTileEntityId);
    }

    public int getParallel(){
        return 64;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(GuiTextures.BACKGROUND, 180, 120)
                .widget(new SlotWidget(itemInventory, 0, 10, 10, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 0, 28, 10, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 0, 10, 28, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .widget(new SlotWidget(itemInventory, 0, 28, 28, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT))
                .label(10, 5, getMetaFullName())
                .build(getHolder(), entityPlayer);
    }
}
