package gtwp.loaders.recipes.multi;

import net.minecraft.init.Blocks;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gtwp.api.recipes.GTWPRecipeMaps.*;

public class GreenHouseRecipes {

    public static void init()
    {
        GREENHOUSE.recipeBuilder()
                .circuitMeta(1)
                .input(Blocks.SAND, 20)
                .fluidInputs(Water.getFluid(3240))
                .output(log, Wood, 60)
                .duration(600)
                .EUt(32)
                .buildAndRegister();

        GREENHOUSE.recipeBuilder()
                .circuitMeta(2)
                .fluidInputs(Water.getFluid(3240))
                .output(log, Wood, 60)
                .duration(1200)
                .EUt(32)
                .buildAndRegister();
    }
}
