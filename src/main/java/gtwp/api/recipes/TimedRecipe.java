package gtwp.api.recipes;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import scala.collection.immutable.IntMap;

import java.util.List;

public class TimedRecipe extends Recipe {

    private final IntMap<ItemStack> timedOutputs;
    private final IntMap<FluidStack> timedFluidOutputs;

    public TimedRecipe(List<CountableIngredient> inputs, List<ItemStack> outputs, List<ChanceEntry> chancedOutputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs, IntMap<ItemStack> timedOutputs, IntMap<FluidStack> timedFluidOutputs, int duration, int EUt, boolean hidden) {
        super(inputs, outputs, chancedOutputs, fluidInputs, fluidOutputs, duration, EUt, hidden);
        this.timedOutputs = timedOutputs;
        this.timedFluidOutputs = timedFluidOutputs;
    }

    public IntMap<ItemStack> getTimedOutputs() {
        return timedOutputs;
    }

    public IntMap<FluidStack> getTimedFluidOutputs() {
        return timedFluidOutputs;
    }
}
