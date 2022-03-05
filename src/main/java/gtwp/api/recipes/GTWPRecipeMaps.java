package gtwp.api.recipes;

import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.IntCircuitRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.builders.TimedRecipeBuilder;

public class GTWPRecipeMaps {

    //Simple machines recipe maps

    //Multi blocks recipe maps

    public static final RecipeMap<IntCircuitRecipeBuilder> GREENHOUSE = new RecipeMap<>(
            "greenhouse",
            1,1,
            1,1,
            1,1,
            0,0,
            new IntCircuitRecipeBuilder(), false);

    public static final RecipeMap<TimedRecipeBuilder> PYROLYSE = new RecipeMap<>(
            "pyrolyse",
            1,1,
            0,1,
            0,1,
            0, 5,
            new TimedRecipeBuilder(), false);

    public static final RecipeMap<SimpleRecipeBuilder> SAWMILL = new RecipeMap<>(
            "sawmill",
            1,1,
            1,2,
            0,1,
            0,0,
            new SimpleRecipeBuilder(), false);
}
