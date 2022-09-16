package precisioncore.api.capability.impl;

import gregtech.api.capability.impl.MultiblockFuelRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import net.minecraftforge.fluids.FluidStack;
import precisioncore.common.metatileentities.multi.nuclear.AdvancedTurbine;

public class AdvancedTurbineLogic extends MultiblockFuelRecipeLogic {

    public AdvancedTurbineLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public AdvancedTurbine getMetaTileEntity() {
        return (AdvancedTurbine) super.getMetaTileEntity();
    }

    @Override
    protected void updateRecipeProgress() {
        if (canRecipeProgress) {
            // turbines can void energy
            drawEnergy(recipeEUt, false);
            //as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        }
    }

    public FluidStack getInputFluidStack() {
        // Previous Recipe is always null on first world load, so try to acquire a new recipe
        if (previousRecipe == null) {
            Recipe recipe = findRecipe(Integer.MAX_VALUE, getInputInventory(), getInputTank());

            return recipe == null ? null : getInputTank().drain(new FluidStack(recipe.getFluidInputs().get(0).getInputFluidStack().getFluid(), Integer.MAX_VALUE), false);
        }
        FluidStack fuelStack = previousRecipe.getFluidInputs().get(0).getInputFluidStack();
        return getInputTank().drain(new FluidStack(fuelStack.getFluid(), Integer.MAX_VALUE), false);
    }

    @Override
    protected boolean prepareRecipe(Recipe recipe) {
        FluidStack recipeFluidStack = recipe.getFluidInputs().get(0).getInputFluidStack();
        // Null check fluid here, since it can return null on first join into world or first form
        FluidStack inputFluid = getInputFluidStack();
        if (inputFluid == null)
            return false;

        int parallel = (int) Math.min(Math.floor((float) getMaxVoltage() / Math.abs(recipe.getEUt())), Math.floor((float) inputFluid.amount / recipeFluidStack.amount));

        //rebuild the recipe and adjust voltage to match the turbine
        RecipeBuilder<?> recipeBuilder = getRecipeMap().recipeBuilder();
        recipeBuilder.append(recipe, parallel, false)
                .EUt(parallel*Math.abs(recipe.getEUt()));
        applyParallelBonus(recipeBuilder);
        recipe = recipeBuilder.build().getResult();

        if (recipe != null && setupAndConsumeRecipeInputs(recipe, getInputInventory())) {
            setupRecipe(recipe);
            return true;
        }
        return false;
    }
}
