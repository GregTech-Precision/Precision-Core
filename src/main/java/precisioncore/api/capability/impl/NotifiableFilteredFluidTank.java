package precisioncore.api.capability.impl;

import gregtech.api.capability.INotifiableHandler;
import gregtech.api.capability.impl.NotifiableFluidTank;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public class NotifiableFilteredFluidTank extends NotifiableFluidTank implements INotifiableHandler {

    Predicate<FluidStack> fluidPredicate;

    public NotifiableFilteredFluidTank(int capacity, MetaTileEntity entityToNotify, boolean isExport) {
        super(capacity, entityToNotify, isExport);
    }

    public NotifiableFilteredFluidTank setFluidPredicate(Predicate<FluidStack> fluidPredicate) {
        this.fluidPredicate = fluidPredicate;
        return this;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return super.canFillFluidType(fluid) && fluidPredicate.test(fluid);
    }
}
