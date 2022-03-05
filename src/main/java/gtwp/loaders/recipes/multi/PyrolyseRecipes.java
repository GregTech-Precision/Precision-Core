package gtwp.loaders.recipes.multi;

import gregtech.api.fluids.MetaFluids;
import gregtech.api.unification.material.Materials;
import gtwp.api.recipes.GTWPRecipeMaps;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.unification.material.Materials.*;

import static gregtech.api.unification.ore.OrePrefix.*;
import static gtwp.api.recipes.GTWPRecipeMaps.PYROLYSE;

public class PyrolyseRecipes {

    public static void init(){
        PYROLYSE.recipeBuilder()
                .input(log, Wood, 16)
                .timedFluidOutput(CarbonMonoxide.getFluid(72), 7*20)
                .timedFluidOutput(Hydrogen.getFluid(288), 14*20)
                .timedFluidOutput(Methane.getFluid(144), 21*20)
                .timedFluidOutput(CarbonDioxide.getFluid(216), 28*20)
                .fluidOutputs(WoodTar.getFluid(1440))
                .output(gem, Charcoal, 5)
                .duration(35*20).EUt(128)
                .buildAndRegister();
    }
}























