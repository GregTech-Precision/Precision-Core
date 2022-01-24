package gtwpp.machines.Entities;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.Textures;
import gregtech.api.util.GTLog;
import gtwpp.GTWP;
import net.minecraft.util.ResourceLocation;

public class NewMetaTileEntities {
    public static SimpleMachineMetaTileEntity ASSEMBLER_LUV;

    public static void init() {
        GTLog.logger.info("GT:WP Registering NewMetaTileEntities");
        ASSEMBLER_LUV = GregTechAPI.registerMetaTileEntity(22228, new SimpleMachineMetaTileEntity(location("assembler.luv"), RecipeMaps.ASSEMBLER_RECIPES, Textures.ASSEMBLER_OVERLAY, 6));
   }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWP.MODID, name);
    }

}