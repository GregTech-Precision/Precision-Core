package precisioncore.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import precisioncore.common.metatileentities.multi.parallel.ParallelComputer;

import java.util.List;

public class ParallelComputerLogic extends AbstractRecipeLogic {

    private boolean isWorking = false;

    public ParallelComputerLogic(ParallelComputer parallelComputer) {
        super(parallelComputer, null);
    }

    @Override
    public ParallelComputer getMetaTileEntity() {
        return ((ParallelComputer) super.getMetaTileEntity());
    }

    @Override
    public boolean isWorkingEnabled() {
        return drawEnergy((int)(GTValues.V[5]+getMetaTileEntity().getCurrentParallelPoints()), true) && getMaxVoltage()*2 >= GTValues.V[5] + getMetaTileEntity().getCurrentParallelPoints();
    }

    @Override
    protected long getEnergyInputPerSecond() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        return input.isEmpty() ? 0 : input.get(0).getInputPerSec();
    }

    @Override
    protected long getEnergyStored() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        return input.isEmpty() ? 0 : input.get(0).getEnergyStored();
    }

    @Override
    protected long getEnergyCapacity() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        return input.isEmpty() ? 0 : input.get(0).getEnergyCapacity();
    }

    @Override
    protected long getMaxVoltage() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        return input.isEmpty() ? 0 : input.get(0).getInputVoltage();
    }

    @Override
    protected boolean drawEnergy(int amount, boolean simulate) {
        List<IEnergyContainer> energy = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        if (!energy.isEmpty()) {
            if (simulate) {
                return energy.get(0).getEnergyStored() - amount >= 0;
            } else {
                return energy.get(0).changeEnergy(-amount) >= 0;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if(isWorkingEnabled()){
            isWorking = true;
            drawEnergy((int)(GTValues.V[5]+getMetaTileEntity().getCurrentParallelPoints()), false);
        } else isWorking = false;
    }

    @Override
    public boolean isWorking() {
        return isWorking;
    }
}
