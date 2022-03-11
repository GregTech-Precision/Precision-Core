package gtwp.api.metatileentities;

import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTLog;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.IParallelMultiblock;
import gtwp.api.capability.impl.ParallelRecipeLogic;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.GTWPUtility;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class GTWPRecipeMapMultiblockController extends MultiMapMultiblockController implements IParallelMultiblock {

    public GTWPRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
    }

    public GTWPRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new ParallelRecipeLogic(this);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int getMaxParallel() {
        List<IParallelHatch> parallel = getAbilities(GTWPMultiblockAbility.PARALLEL_HATCH);
        return parallel.isEmpty() ? 1 : GTWPUtility.clamp(1,256, parallel.get(0).getParallel());
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn, checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        if (isParallel())
            predicate = predicate.or(abilities(GTWPMultiblockAbility.PARALLEL_HATCH).setMaxGlobalLimited(1,1));
        return predicate;
    }
}
