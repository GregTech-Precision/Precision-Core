package precisioncore.api.capability.impl;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import precisioncore.api.capability.IParallelMultiblock;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;
import precisioncore.api.utils.PrecisionUtility;

public class ParallelRecipeLogic extends MultiblockRecipeLogic {

    public ParallelRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public int getParallelLimit() {
        if(metaTileEntity instanceof IParallelMultiblock && ((IParallelMultiblock) metaTileEntity).isParallel())
            return PrecisionUtility.clamp(1,256,((IParallelMultiblock) metaTileEntity).getMaxParallel());
        return 1;
    }

    @Override
    public PrecisionRecipeMapMultiblockController getMetaTileEntity() {
        return ((PrecisionRecipeMapMultiblockController) super.getMetaTileEntity());
    }
}
