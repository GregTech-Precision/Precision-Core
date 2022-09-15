package precisioncore.loaders.recipes.multi;

import gregtech.api.unification.material.Materials;
import precisioncore.api.unification.PrecisionMaterials;

import static precisioncore.api.recipes.PrecisionRecipeMaps.ADVANCED_TURBINE;

public class AdvancedTurbineRecipes {

    public static void init(){
        ADVANCED_TURBINE.recipeBuilder()
                .fluidInputs(PrecisionMaterials.SuperHeatedSteam.getFluid(640))
                .fluidOutputs(Materials.DistilledWater.getFluid(1))
                .EUt(-64).duration(10)
                .buildAndRegister();

        ADVANCED_TURBINE.recipeBuilder()
                .fluidInputs(Materials.Steam.getFluid(640))
                .fluidOutputs(Materials.DistilledWater.getFluid(1))
                .EUt(-32).duration(10)
                .buildAndRegister();
    }
}
