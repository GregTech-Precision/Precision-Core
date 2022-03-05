package gtwp.loaders.recipes;

import gtwp.loaders.recipes.multi.GreenHouseRecipes;
import gtwp.loaders.recipes.multi.PyrolyseRecipes;

public class GTWPRecipeLoader {

    public static void init()
    {
        //SINGLEBLOCKS RECIPE MAP LOADER

        //MULTIBLOCKS RECIPE MAP LOADER
        GreenHouseRecipes.init();
        PyrolyseRecipes.init();
    }

}
