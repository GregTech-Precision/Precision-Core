package precisioncore.api.recipes;

import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.IntCircuitRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;

public class PrecisionRecipeMaps {

    //Simple machines recipe maps

    //Multi blocks recipe maps

    public static final RecipeMap<IntCircuitRecipeBuilder> GREENHOUSE = new RecipeMap<>(
            "greenhouse",
            1,1,
            1,1,
            1,1,
            0,0,
            new IntCircuitRecipeBuilder(), false);

    public static final RecipeMap<SimpleRecipeBuilder> PYROLYSE = new RecipeMap<>(
            "pyrolyse",
            0,1,
            0,6,
            0,1,
            0, 6,
            new SimpleRecipeBuilder(), false);

    public static final RecipeMap<SimpleRecipeBuilder> SAWMILL = new RecipeMap<>(
            "sawmill",
            1,1,
            1,2,
            0,1,
            0,0,
            new SimpleRecipeBuilder(), false);

    public static final RecipeMap<FuelRecipeBuilder> ADVANCED_TURBINE = new RecipeMap<>(
            "advanced_turbine",
            0, 0,
            0, 0,
            1, 1,
            0, 1,
            new FuelRecipeBuilder(), false);
}
