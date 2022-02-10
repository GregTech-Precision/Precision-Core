package gtwp.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.IntCircuitRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;

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
}
