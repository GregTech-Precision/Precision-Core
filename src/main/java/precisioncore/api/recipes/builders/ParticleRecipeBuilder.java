package precisioncore.api.recipes.builders;

import gregtech.api.recipes.RecipeBuilder;
import precisioncore.api.unification.material.PrecisionMaterials;

public class ParticleRecipeBuilder extends RecipeBuilder<ParticleRecipeBuilder> {

    ParticleRecipeBuilder particle(int amount){
        return this.fluidInputs(PrecisionMaterials.MatrixParticle.getFluid(amount));
    }
}
