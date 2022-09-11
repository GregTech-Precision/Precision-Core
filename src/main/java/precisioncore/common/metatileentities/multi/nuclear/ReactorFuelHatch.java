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
import ic2.core.item.ItemGradualInt;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.gui.PrecisionGUITextures;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.items.behaviors.NuclearFuelBehavior;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public class ReactorFuelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IReactorHatch>, IReactorHatch {

    private int rodLevel = 0;
    private final NuclearFuelInventoryHolder holder;

    public ReactorFuelHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5);
        this.holder = new NuclearFuelInventoryHolder();
    }

    @Override
    public int getRodLevel() {
        return hasRod() ? rodLevel : 0;
    }

    @Override
    public int getRodModifier() {
        ItemStack stack = holder.getStackInSlot(0);
        if(stack.isEmpty())
            return 0;
        else {
            NuclearFuelBehavior behavior = NuclearFuelBehavior.getInstanceFor(stack);
            if(behavior != null)
                return behavior.getFuelModifier();
            else {
                if(stack.getItem() instanceof ItemReactorMOX)
                    return ((ItemReactorMOX) stack.getItem()).numberOfCells * 4;
                else
                    return ((ItemReactorUranium) stack.getItem()).numberOfCells;
            }
        }
    }



    @Override
    public void downRod() {
        int newRodLevel = Math.min(0, rodLevel + 1);
        if(newRodLevel != this.rodLevel && !getWorld().isRemote) {
            writeCustomData(PrecisionDataCodes.ROD_UPDATE, buf -> buf.writeVarInt(this.rodLevel));
        }
        this.rodLevel = newRodLevel;
        ((Reactor) getController()).notifyOnRodChanges();
    }

    @Override
    public void upRod() {
        int newRodLevel = Math.max(0, rodLevel - 1);
        if(newRodLevel != this.rodLevel && !getWorld().isRemote) {
            writeCustomData(PrecisionDataCodes.ROD_UPDATE, buf -> buf.writeVarInt(this.rodLevel));
        }
        this.rodLevel = newRodLevel;
        ((Reactor) getController()).notifyOnRodChanges();
    }

    @Override
    public boolean isMOX() {
        ItemStack stack = holder.getStackInSlot(0);
        if(stack.isEmpty())
            return false;
        return stack.getItem() instanceof ItemReactorMOX || NuclearFuelBehavior.getInstanceFor(holder.getStackInSlot(0)).isMOX();
    }

    @Override
    public boolean hasRod() {
        return !holder.getStackInSlot(0).isEmpty();
    }

    @Override
    public void depleteRod() {
        ItemStack stack = holder.getStackInSlot(0);
        if(stack.isEmpty())
            return;
        NuclearFuelBehavior beh = NuclearFuelBehavior.getInstanceFor(stack);
        if(beh != null)
            beh.applyRodDamage(stack, getRodLevel());
        else
            ((ItemGradualInt) stack.getItem()).applyCustomDamage(stack, getRodLevel(), null);
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
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        T result = super.getCapability(capability, side);
        if(result != null)
            return result;
        if(capability == PrecisionCapabilities.CAPABILITY_ROD){
            return PrecisionCapabilities.CAPABILITY_ROD.cast(this);
        }
        return null;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.ROD_UPDATE){
            this.rodLevel = buf.readVarInt();
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(this.rodLevel);
        buf.writeCompoundTag(holder.serializeNBT());
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.rodLevel = buf.readVarInt();
        try {
            this.holder.deserializeNBT(buf.readCompoundTag());
        } catch (IOException e) {
            // :3
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("rodLevel", this.rodLevel);
        data.setTag("inventory", holder.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.rodLevel = data.getInteger("rodLevel");
        this.holder.deserializeNBT(data.getCompoundTag("inventory"));
    }

    private class NuclearFuelInventoryHolder extends ItemStackHandler {

        NuclearFuelInventoryHolder() {
            super(1);
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        public boolean isRodValid(ItemStack stack){
            Item item = stack.getItem();
            return item instanceof ItemReactorUranium;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return (isRodValid(stack) || NuclearFuelBehavior.getInstanceFor(stack) != null) && super.isItemValid(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            scheduleRenderUpdate();
            ((Reactor) getController()).notifyOnRodChanges();
        }
    }
}
