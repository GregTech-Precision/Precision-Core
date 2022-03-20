package gtwp.api.capability.impl;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gtwp.common.metatileentities.multi.parallel.ParallelComputer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ParallelComputerLogic extends AbstractRecipeLogic {

    public ParallelComputerLogic(ParallelComputer tileEntity) {
        super(tileEntity, null);
        this.progressTime = 0;
        this.maxProgressTime = 20;
    }

    @Override
    public ParallelComputer getMetaTileEntity() {
        return ((ParallelComputer) super.getMetaTileEntity());
    }

    @Override
    public boolean isWorkingEnabled() {
        return getEnergyStored() > getMaxVoltage();
    }

    @Override
    protected long getEnergyInputPerSecond() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        int perSec = 0;
        for(IEnergyContainer energy : input)
            perSec += energy.getInputPerSec();
        return perSec;
    }

    @Override
    protected long getEnergyStored() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        int stored = 0;
        for(IEnergyContainer energy : input)
            stored += energy.getEnergyStored();
        return stored;
    }

    @Override
    protected long getEnergyCapacity() {
        List<IEnergyContainer> input = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        int capacity = 0;
        for(IEnergyContainer energy : input)
            capacity += energy.getEnergyCapacity();
        return capacity;
    }

    @SideOnly(Side.SERVER)
    @Override
    protected boolean drawEnergy(int i, boolean b) {
        List<IEnergyContainer> energy = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        if(!energy.isEmpty())
            energy.get(0).removeEnergy(i);
        return true;
    }

    @Override
    protected long getMaxVoltage() {
        List<IEnergyContainer> energy = getMetaTileEntity().getAbilities(MultiblockAbility.INPUT_ENERGY);
        return energy.isEmpty() ? 0 : energy.get(0).getInputVoltage();
    }

    @Override
    public void update() {
        if(progressTime == 0 && isWorkingEnabled())
            progressTime = 1;
    }

    @Override
    public boolean isWorking() {
        return progressTime > 0;
    }

    @Override
    protected void updateRecipeProgress() {
        if(++progressTime > maxProgressTime)
            completeRecipe();
        drawEnergy(8192, false);
    }

    @Override
    protected void completeRecipe() {
        progressTime = 0;
    }
}
