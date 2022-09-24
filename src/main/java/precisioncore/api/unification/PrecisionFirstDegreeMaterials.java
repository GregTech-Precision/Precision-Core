package precisioncore.api.unification;

import gregtech.api.fluids.fluidType.FluidTypes;
import gregtech.api.unification.material.Material;

import static gregtech.api.unification.material.Materials.Hydrogen;
import static gregtech.api.unification.material.Materials.Oxygen;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static precisioncore.api.unification.PrecisionMaterials.MatrixParticle;
import static precisioncore.api.unification.PrecisionMaterials.SuperHeatedSteam;

public class PrecisionFirstDegreeMaterials {

    public static void init(){
        MatrixParticle = new Material.Builder(20000, "matrix_particle")
                .fluid().color(0x804000).build();

        SuperHeatedSteam = new Material.Builder(20001, "super_heated_steam")
                .fluid(FluidTypes.GAS)
                .flags(DISABLE_DECOMPOSITION)
                .components(Hydrogen, 2, Oxygen, 1)
                .fluidTemp(603)
                .color(0xA5A5A5)
                .build();
    }
}
