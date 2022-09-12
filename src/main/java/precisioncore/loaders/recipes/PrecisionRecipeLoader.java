package precisioncore.loaders.recipes;

import precisioncore.loaders.recipes.multi.AdvancedTurbineRecipes;
import precisioncore.loaders.recipes.multi.GreenHouseRecipes;
import precisioncore.loaders.recipes.multi.PyrolyseRecipes;

public class PrecisionRecipeLoader {

    public static void init()
    {
        //SINGLEBLOCKS RECIPE MAP LOADER

        //MULTIBLOCKS RECIPE MAP LOADER
        GreenHouseRecipes.init();
        PyrolyseRecipes.init();
        AdvancedTurbineRecipes.init();
    }

}
