package precisioncore.api.unification;

import gregtech.api.unification.material.Material;

public class PrecisionMaterials {

    //Materials will go from 20000 to 31999

    public static Material MatrixParticle;
    public static Material SuperHeatedSteam;

    public static void init(){
        PrecisionFirstDegreeMaterials.init();
    }
}
