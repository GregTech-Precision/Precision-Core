package gtwp.common.metatileentities;

import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.GTWP;
import gtwp.common.metatileentities.longpipes.PipelineEnergy;
import gtwp.common.metatileentities.longpipes.PipelineFluid;
import gtwp.common.metatileentities.multi.processing.Greenhouse;
import gtwp.common.metatileentities.multi.processing.Sawmill;
import gtwp.common.metatileentities.multi.tanks.MultiFluidMultiTank;
import gtwp.common.metatileentities.multi.processing.PyrolyseOven;
import gtwp.common.metatileentities.multi.tanks.SingleFluidMultiTank;
import gtwp.common.metatileentities.multi.tanks.MetaTileEntityIOHatch;
import gtwp.common.metatileentities.multi.multiblockparts.MetaTileEntityMEItemOutputHatch;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class GTWPMetaTileEntities {

    private static int id = 11000;

    //HATCHES
    public static MetaTileEntityIOHatch IO_HATCH;
    public static MetaTileEntityMEItemOutputHatch ME_HATCH;
    //CONTROLLERS
    public static SingleFluidMultiTank SINGLE_FLUID_MULTI_TANK;
    public static MultiFluidMultiTank MULTI_FLUID_MULTI_TANK;
    public static Greenhouse GREENHOUSE;
    public static PyrolyseOven PYROLYSE_OVEN;
    public static Sawmill SAWMILL;

    public static PipelineFluid PIPELINEFLUID;
    public static PipelineEnergy PIPELINEENERGY;


    public static void init() {
        GTLog.logger.info("GT:WP Registering New Meta Tile Entities");

        //HATCHES
        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityIOHatch(location("fluid_hatch.io")));

        ME_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityMEItemOutputHatch(location("item_hatch.me")));

        //CONTROLLERS
        SINGLE_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new SingleFluidMultiTank(location("fluid_tank.single")));
        MULTI_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new MultiFluidMultiTank(location("fluid_tank.multi")));
        GREENHOUSE = MetaTileEntities.registerMetaTileEntity(id++, new Greenhouse(location("greenhouse")));
        PYROLYSE_OVEN = MetaTileEntities.registerMetaTileEntity(1004, new PyrolyseOven(location("pyrolyse_oven")));
        SAWMILL = MetaTileEntities.registerMetaTileEntity(id++, new Sawmill(location("sawmill")));
        //SIMPLE MACHINES
        PIPELINEFLUID = MetaTileEntities.registerMetaTileEntity(id++, new PipelineFluid(location("pipelinefluid")));
        PIPELINEENERGY = MetaTileEntities.registerMetaTileEntity(id++, new PipelineEnergy(location("pipelineenergy"), 0));
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