package gtwp.api.capability.impl;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.logic.IParallelableRecipeLogic;
import gtwp.api.capability.IParallelMultiblock;
import gtwp.api.metatileentities.GTWPRecipeMapMultiblockController;

public class ParallelRecipeLogic extends MultiblockRecipeLogic {

    public ParallelRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public int getParallelLimit() {
        if(metaTileEntity instanceof IParallelMultiblock && ((IParallelMultiblock) metaTileEntity).isParallel())
            return ((IParallelMultiblock) metaTileEntity).getMaxParallel();
        return 1;
    }

    @Override
    public GTWPRecipeMapMultiblockController getMetaTileEntity() {
        return ((GTWPRecipeMapMultiblockController) super.getMetaTileEntity());
    }
}
