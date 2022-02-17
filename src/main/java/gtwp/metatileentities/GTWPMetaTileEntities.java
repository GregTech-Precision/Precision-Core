package gtwp.metatileentities;

import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.GTWP;
import gtwp.metatileentities.longpipes.PipelineBase;
import gtwp.metatileentities.multi.Greenhouse;
import gtwp.metatileentities.multi.MultiFluidMultiTank;
import gtwp.metatileentities.multi.SingleFluidMultiTank;
import gtwp.metatileentities.multi.multiblockparts.MetaTileEntityIOHatch;
import gtwp.metatileentities.multi.multiblockparts.MetaTileEntityMEItemOutputHatch;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class GTWPMetaTileEntities {

    private static int id = 11000;
    public static MetaTileEntityIOHatch IO_HATCH;
    public static SingleFluidMultiTank SINGLE_FLUID_MULTI_TANK;
    public static MultiFluidMultiTank MULTI_FLUID_MULTI_TANK;
    public static Greenhouse GREENHOUSE;
    public static MetaTileEntityMEItemOutputHatch ME_HATCH;
    public static PipelineBase PIPELINEBASE;


    public static void init() {
        GTLog.logger.info("GT:WP Registering New Meta Tile Entities");

        //Multiblocks
        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityIOHatch(location("fluid_hatch.io")));
        SINGLE_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new SingleFluidMultiTank(location("fluid_tank.single")));
        MULTI_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new MultiFluidMultiTank(location("fluid_tank.multi")));

        ME_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityMEItemOutputHatch(location("item_hatch.me")));

        GREENHOUSE = MetaTileEntities.registerMetaTileEntity(id++, new Greenhouse(location("greenhouse")));
        //Simple machines
        PIPELINEBASE = MetaTileEntities.registerMetaTileEntity(id++, new PipelineBase(location("pipelinebase")));
    }

    private static void registerSimpleMetaTileEntity(SimpleMachineMetaTileEntity[] machines,
                                                     int startId,
                                                     String name,
                                                     RecipeMap<?> map,
                                                     ICubeRenderer texture,
                                                     boolean hasFrontFacing,
                                                     Function<Integer, Integer> tankScalingFunction) {
        MetaTileEntities.registerSimpleMetaTileEntity(machines, startId, name, map, texture, hasFrontFacing, GTWPMetaTileEntities::location, tankScalingFunction);
    }

    private static void registerSimpleMetaTileEntity(SimpleMachineMetaTileEntity[] machines,
                                                     int startId,
                                                     String name,
                                                     RecipeMap<?> map,
                                                     ICubeRenderer texture,
                                                     boolean hasFrontFacing) {
        registerSimpleMetaTileEntity(machines, startId, name, map, texture, hasFrontFacing, GTUtility.defaultTankSizeFunction);
    }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWP.MODID, name);
    }
}