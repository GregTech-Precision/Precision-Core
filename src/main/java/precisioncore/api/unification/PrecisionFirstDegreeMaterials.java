package precisioncore.api.unification;

import gregtech.api.unification.material.Material;

import static precisioncore.api.unification.PrecisionMaterials.*;

public class PrecisionFirstDegreeMaterials {

    public static void init(){
        MatrixParticle = new Material.Builder(20000, "matrix_particle")
                .fluid().color(0x804000).build();
    }
}
