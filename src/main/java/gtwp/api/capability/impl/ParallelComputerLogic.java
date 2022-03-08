package gtwp.api.capability.impl;

import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.RecipeMap;

public class ParallelComputerLogic extends AbstractRecipeLogic {

    public ParallelComputerLogic(MetaTileEntity tileEntity, RecipeMap<?> recipeMap) {
        super(tileEntity, recipeMap);
    }

    @Override
    protected long getEnergyInputPerSecond() {
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        return 0;
    }

    @Override
    protected long getEnergyCapacity() {
        return 0;
    }

    @Override
    protected boolean drawEnergy(int i, boolean b) {
        return false;
    }

    @Override
    protected long getMaxVoltage() {
        return 0;
    }
}
