package precisioncore.loaders.recipes.multi;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static precisioncore.api.recipes.PrecisionRecipeMaps.PYROLYSE;

public class PyrolyseRecipes {

    public static void init(){
        PYROLYSE.recipeBuilder()
                .input(dust, Wood, 8)
                .fluidOutputs(CarbonMonoxide.getFluid(72),
                        Hydrogen.getFluid(288),
                        Methane.getFluid(144),
                        CarbonDioxide.getFluid(216),
                        WoodTar.getFluid(1440))
                .output(gem, Charcoal, 5)
                .duration(35*20).EUt(128)
                .buildAndRegister();

        //CH4 -> C + 2H2
        PYROLYSE.recipeBuilder()
                .fluidInputs(Methane.getFluid(1000))
                .fluidOutputs(Hydrogen.getFluid(4000))
                .output(dust, Carbon)
                .duration(20*5).EUt(128)
                .buildAndRegister();

        //C2H4 -> C2H2 + H2
        PYROLYSE.recipeBuilder()
                .fluidInputs(Ethylene.getFluid(1000))
                .fluidOutputs()
                .buildAndRegister();

        //C2H6 -> C2H4 + H2
        PYROLYSE.recipeBuilder()
                .fluidInputs(Ethane.getFluid(1000))
                .fluidOutputs(Ethylene.getFluid(1000))
                .fluidOutputs(Hydrogen.getFluid(2000))
                .duration(20*8).EUt(128)
                .buildAndRegister();

        //C3H6 -> C2H2 + H2

        //C3H8 -> C2H4 + CH4
        PYROLYSE.recipeBuilder()
                .fluidInputs(Propane.getFluid(1000))
                .fluidOutputs(Ethylene.getFluid(1000))
                .fluidOutputs(Methane.getFluid(1000))
                .duration(20*11).EUt(128)
                .buildAndRegister();

        //C4H10 -> C2H6 + C2H4
        PYROLYSE.recipeBuilder()
                .fluidInputs(Butane.getFluid(1000))
                .fluidOutputs(Ethylene.getFluid(1000))
                .fluidOutputs(Ethane.getFluid(1000))
                .duration(14*20).EUt(128)
                .buildAndRegister();



    }
}























