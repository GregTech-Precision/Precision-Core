package gtwp.machines.Entities;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.Textures;
import gregtech.api.util.GTLog;
import gtwp.GTWPMod;
import net.minecraft.util.ResourceLocation;

public class NewMetaTileEntities {
    public static SimpleMachineMetaTileEntity ASSEMBLER_LUV;
    public static gtwp.metatileentity.multiblock.MetaTileEntityMultipleFluidHatch QUADRUPLE_HATCH;

    public static void init() {
        GTLog.logger.info("GT:WP Registering NewMetaTileEntities");
        ASSEMBLER_LUV = GregTechAPI.registerMetaTileEntity(22228, new SimpleMachineMetaTileEntity(location("assembler.luv"), RecipeMaps.ASSEMBLER_RECIPES, Textures.ASSEMBLER_OVERLAY, 6));
        QUADRUPLE_HATCH = GregTechAPI.registerMetaTileEntity(2228, new gtwp.metatileentity.multiblock.MetaTileEntityMultipleFluidHatch(location("quadruple_hatch"), 4));
   }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWPMod.MODID, name);
    }

}