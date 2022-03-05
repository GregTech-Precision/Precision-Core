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
import gtwp.common.metatileentities.multi.Greenhouse;
import gtwp.common.metatileentities.multi.MultiFluidMultiTank;
import gtwp.common.metatileentities.multi.PyrolyseOven;
import gtwp.common.metatileentities.multi.SingleFluidMultiTank;
import gtwp.common.metatileentities.multi.multiblockparts.MetaTileEntityIOHatch;
import gtwp.common.metatileentities.multi.multiblockparts.MetaTileEntityMEItemOutputHatch;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class GTWPMetaTileEntities {

    private static int id = 11000;
    public static MetaTileEntityIOHatch IO_HATCH;
    public static SingleFluidMultiTank SINGLE_FLUID_MULTI_TANK;
    public static MultiFluidMultiTank MULTI_FLUID_MULTI_TANK;
    public static Greenhouse GREENHOUSE;
    public static PyrolyseOven PYROLYSE_OVEN;
    public static MetaTileEntityMEItemOutputHatch ME_HATCH;
    public static PipelineFluid PIPELINEFLUID;
    public static PipelineEnergy PIPELINEENERGY;


    public static void init() {
        GTLog.logger.info("GT:WP Registering New Meta Tile Entities");

        //Multiblocks
        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityIOHatch(location("fluid_hatch.io")));
        SINGLE_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new SingleFluidMultiTank(location("fluid_tank.single")));
        MULTI_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new MultiFluidMultiTank(location("fluid_tank.multi")));

        ME_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityMEItemOutputHatch(location("item_hatch.me")));

        GREENHOUSE = MetaTileEntities.registerMetaTileEntity(id++, new Greenhouse(location("greenhouse")));

        PYROLYSE_OVEN = MetaTileEntities.registerMetaTileEntity(1004, new PyrolyseOven(location("pyrolyse_oven")));
        //Simple machines
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