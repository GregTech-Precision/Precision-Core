package gtwp.metatileentities;

import gregtech.api.util.GTLog;
import gtwp.metatileentities.multi.multiblockparts.MetaTileEntityIOHatch;
import gtwp.GTWP;
import gtwp.metatileentities.multi.MultiFluidMultiTank;
import gtwp.metatileentities.multi.SingleFluidMultiTank;
import gtwp.metatileentities.multi.multiblockparts.MetaTileEntityMultiIOHatch;
import net.minecraft.util.ResourceLocation;
import gregtech.common.metatileentities.MetaTileEntities;

public class GTWPMetaTileEntities {

    private static int id = 11000;
    public static MetaTileEntityIOHatch IO_HATCH;
    public static MetaTileEntityMultiIOHatch MULTI_IO_HATCH;
    public static SingleFluidMultiTank SINGLE_FLUID_MULTI_TANK;
    public static MultiFluidMultiTank MULTI_FLUID_MULTI_TANK;
    
    public static void init()
    {
        GTLog.logger.info("GT:WP Registering New Meta Tile Entities");

        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityIOHatch(location("fluid_hatch.io")));
        MULTI_IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityMultiIOHatch(location("fluid_hatch.io_multi")));
        SINGLE_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new SingleFluidMultiTank(location("fluid_tank.single")));
        MULTI_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new MultiFluidMultiTank(location("fluid_tank.multi")));
    }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWP.MODID, name);
    }



}