package precisioncore.common.metatileentities.multi.matrixsystem;

import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.NotifiableFluidTank;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;

import javax.annotation.Nonnull;
import java.util.List;

public class MatrixPatricleHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<MatrixPatricleHatch> {

    private static boolean isExport;

    public MatrixPatricleHatch(ResourceLocation metaTileEntityId, boolean isExport) {
        super(metaTileEntityId, 3);
        this.isExport = isExport;
        itemInventory = new MPItemHandler();
        this.fluidInventory = new NotifiableFluidTank(32000, this, isExport);
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MatrixPatricleHatch(metaTileEntityId, isExport);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public MultiblockAbility<MatrixPatricleHatch> getAbility() {
        return isExport ? PrecisionMultiblockAbility.MATRIX_PARTICLE_EXPORT : PrecisionMultiblockAbility.MATRIX_PARTICLE_IMPORT;
    }

    @Override
    public void registerAbilities(List<MatrixPatricleHatch> list) {
        list.add(this);
    }

    private class MPItemHandler extends ItemStackHandler {

        @Override
        public void setSize(int size) {
            super.setSize(size);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0; //check if item in slot 0 is MP cell
        }
    }
}
