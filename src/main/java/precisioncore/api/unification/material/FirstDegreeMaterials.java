package precisioncore.api.unification.material;

import gregtech.api.unification.material.Material;

import static precisioncore.api.unification.material.PrecisionMaterials.*;

public class FirstDegreeMaterials {

    public static void init(){
        MatrixParticle = new Material.Builder(24000, "matrix_particle")
                .color(0xAA00AA)
                .fluid()
                .build();
    }
}
