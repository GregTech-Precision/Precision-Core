package gtwp.machines;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.Textures;
import gregtech.api.util.GTLog;
import gtwp.GTWP;
import net.minecraft.util.ResourceLocation;
import gtwp.machines.multi.MetaTileEntityMultipleFluidHatch;

public class NewMetaTileEntities {
    public static SimpleMachineMetaTileEntity ASSEMBLER_LUV;
    public static MetaTileEntityMultipleFluidHatch QUADRUPLE_HATCH;
    public static MetaTileEntityMultipleFluidHatch NONUPLE_HATCH;

    public static void init() {
        GTLog.logger.info("GT:WP Registering NewMetaTileEntities");
        ASSEMBLER_LUV = GregTechAPI.registerMetaTileEntity(22228, new SimpleMachineMetaTileEntity(location("assembler.luv"), RecipeMaps.ASSEMBLER_RECIPES, Textures.ASSEMBLER_OVERLAY, 6));
        QUADRUPLE_HATCH = GregTechAPI.registerMetaTileEntity(22229, new MetaTileEntityMultipleFluidHatch(location("fluid_hatch.quadruple"), 4, 4));
        NONUPLE_HATCH = GregTechAPI.registerMetaTileEntity(22230, new MetaTileEntityMultipleFluidHatch(location("fluid_hatch.nonuple"),4,9));
   }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWP.MODID, name);
    }

}