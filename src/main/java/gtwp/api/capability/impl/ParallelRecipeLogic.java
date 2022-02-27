package gtwp.api.capability.impl;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.logic.IParallelableRecipeLogic;
import gtwp.api.capability.IParallelMultiblock;

public class ParallelRecipeLogic extends MultiblockRecipeLogic {

    public ParallelRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public int getParallelLimit() {
        if(metaTileEntity instanceof IParallelMultiblock && ((IParallelMultiblock) metaTileEntity).isParallel())
            ((IParallelMultiblock) metaTileEntity).getMaxParallel();
        return 1;
    }
}
