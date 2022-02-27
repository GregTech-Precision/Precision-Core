package gtwp.api.recipes.builders;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;

public class TimedRecipeBuilder extends RecipeBuilder<TimedRecipeBuilder> {

    public TimedRecipeBuilder(Recipe recipe, RecipeMap<TimedRecipeBuilder> recipeMap){
        super(recipe, recipeMap);
    }

    public TimedRecipeBuilder(){}

    public TimedRecipeBuilder timedFluidOutput(){
        return null;
    }
}
