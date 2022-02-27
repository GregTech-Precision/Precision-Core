package gtwp.api.capability.impl;

import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.common.ConfigHolder;
import gtwp.api.recipes.TimedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import scala.Option;
import scala.collection.immutable.IntMap;

public class TimedOutputRecipeLogic extends ParallelRecipeLogic {

    protected IntMap<ItemStack> timedOutputs;
    protected IntMap<FluidStack> timedFluidOutputs;

    public TimedOutputRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    protected void updateRecipeProgress() {
        if (canRecipeProgress && drawEnergy(recipeEUt, true)) {
            drawEnergy(recipeEUt, false);
            //check output by time
            //as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
            if (this.hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * recipeEUt) {
                this.hasNotEnoughEnergy = false;
            }
        } else if (recipeEUt > 0) {
            //only set hasNotEnoughEnergy if this recipe is consuming recipe
            //generators always have enough energy
            this.hasNotEnoughEnergy = true;
            //if current progress value is greater than 2, decrement it by 2
            if (progressTime >= 2) {
                if (ConfigHolder.machines.recipeProgressLowEnergy) {
                    this.progressTime = 1;
                } else {
                    this.progressTime = Math.max(1, progressTime - 2);
                }
            }
        }
    }

    @Override
    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        if(recipe instanceof TimedRecipe) {
            timedOutputs = ((TimedRecipe) recipe).getTimedOutputs();
            timedFluidOutputs = ((TimedRecipe) recipe).getTimedFluidOutputs();
        }
    }

    protected void outputByTime(int time){
        if(timedOutputs.contains(time))

    }

    @Override
    protected void completeRecipe() {
        super.completeRecipe();
        this.timedOutputs = null;
    }
}
