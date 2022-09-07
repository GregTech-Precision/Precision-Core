package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.gui.PrecisionGUITextures;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactorFuelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IReactorHatch>, IReactorHatch {

    private int rodLevel = 0;
    private final NuclearFuelInventoryHolder holder;
    private static final int DATA_UPDATE = 895;

    public ReactorFuelHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5);
        this.holder = new NuclearFuelInventoryHolder();
    }

    @Override
    public int getRodLevel() {
        return rodLevel;
    }

    @Override
    public void downRod() {
        this.rodLevel=Math.min(10, rodLevel+1);
        writeCustomData(DATA_UPDATE, buf -> buf.writeVarInt(this.rodLevel));
    }

    @Override
    public void upRod() {
        this.rodLevel=Math.max(0, rodLevel-1);
        writeCustomData(DATA_UPDATE, buf -> buf.writeVarInt(this.rodLevel));
    }

    @Override
    public boolean isMOX() {
        return false;
    }

    @Override
    public int depleteRod(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int depleteRod(boolean simulate) {
        return depleteRod(getRodLevel(), simulate);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(PrecisionGUITextures.ROD_HATCH_BACKGROUND, 176, 166)
                .slot(holder, 0, 89, 7, GuiTextures.SLOT)
                .widget(new ClickButtonWidget(69, 7, 18, 18, "", this::clickUpRod))
                .widget(new ClickButtonWidget(69, 61, 18, 18, "", this::clickDownRod))
                .bindPlayerInventory(entityPlayer.inventory)
                .build(getHolder(), entityPlayer);
    }

    private void clickUpRod(Widget.ClickData clickData) {
        upRod();
    }

    private void clickDownRod(Widget.ClickData clickData) {
        downRod();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        (holder.getStackInSlot(0).isEmpty() ? PrecisionTextures.NUCLEAR_FUEL_HATCH_INACTIVE : PrecisionTextures.NUCLEAR_FUEL_HATCH_ACTIVE).renderSided(EnumFacing.UP, renderState, translation, pipeline);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ReactorFuelHatch(metaTileEntityId);
    }

    @Override
    public MultiblockAbility<IReactorHatch> getAbility() {
        return PrecisionMultiblockAbility.REACTOR_HATCH;
    }

    @Override
    public void registerAbilities(List<IReactorHatch> list) {
        list.add(this);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == DATA_UPDATE){
            this.rodLevel = buf.readVarInt();
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(this.rodLevel);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.rodLevel = buf.readVarInt();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("rodLevel", this.rodLevel);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.rodLevel = data.getInteger("rodLevel");
    }

    private class NuclearFuelInventoryHolder extends ItemStackHandler {

        NuclearFuelInventoryHolder() {
            super(1);
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return true; // TODO: add is nuclear fuel checking
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            scheduleRenderUpdate();
        }
    }
}
