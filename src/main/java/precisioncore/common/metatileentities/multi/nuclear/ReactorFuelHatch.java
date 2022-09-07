package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemStackHandler;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;

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
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if(!getWorld().isRemote) {
            if (playerIn.isSneaking()) {
                this.rodLevel--;
                if (this.rodLevel < 0) this.rodLevel = 0;
            } else {
                this.rodLevel++;
                if (this.rodLevel > 10) this.rodLevel = 10;
            }
            writeCustomData(DATA_UPDATE, buf -> buf.writeVarInt(this.rodLevel));
        }
        playerIn.sendMessage(new TextComponentString("Level: " + rodLevel));
        return true;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == DATA_UPDATE){
            this.rodLevel = buf.readVarInt();
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ReactorFuelHatch(metaTileEntityId);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
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
    public int getRodLevel() {
        return rodLevel;
    }

    @Override
    public boolean isMOX() {
        return false;
    }

    @Override
    public int depleteRod(int amount, boolean simulate) {
        return 0;
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
    }
}
