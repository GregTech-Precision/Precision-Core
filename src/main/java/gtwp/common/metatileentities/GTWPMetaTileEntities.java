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
import gtwp.common.metatileentities.multi.parallel.*;
import gtwp.common.metatileentities.multi.processing.Greenhouse;
import gtwp.common.metatileentities.multi.processing.Sawmill;
import gtwp.common.metatileentities.multi.tanks.MultiTank;
import gtwp.common.metatileentities.multi.processing.PyrolyseOven;
import gtwp.common.metatileentities.multi.tanks.SingleTank;
import gtwp.common.metatileentities.multi.tanks.IOHatch;
import gtwp.common.metatileentities.multi.multiblockparts.MetaTileEntityMEItemOutputHatch;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class GTWPMetaTileEntities {

    private static int id = 11000;

    //HATCHES
    public static IOHatch IO_HATCH;
    public static MetaTileEntityMEItemOutputHatch ME_HATCH;
    public static SatelliteTransmitter TRANSMITTER;
    public static SatelliteReceiver RECEIVER;
    public static ParallelHatch PARALLEL_TRANSMITTER;
    public static ParallelHatch PARALLEL_RECEIVER;
    public static ParallelComputerRack PARALLEL_RACK;
    public static CommunicationTower COMMUNICATION_TOWER;
    //CONTROLLERS
    public static SingleTank SINGLE_FLUID_MULTI_TANK;
    public static MultiTank MULTI_FLUID_MULTI_TANK;
    public static Greenhouse GREENHOUSE;
    public static PyrolyseOven PYROLYSE_OVEN;
    public static Sawmill SAWMILL;
    public static Satellite SATELLITE;
    public static ParallelComputer PARALLEL_COMPUTER;

    public static PipelineFluid PIPELINEFLUID;
    public static PipelineEnergy PIPELINEENERGY;


    public static void init() {
        GTLog.logger.info("GT:WP Registering New Meta Tile Entities");

        //HATCHES
        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new IOHatch(location("fluid_hatch.io")));

        ME_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MetaTileEntityMEItemOutputHatch(location("item_hatch.me")));
        TRANSMITTER = MetaTileEntities.registerMetaTileEntity(id++, new SatelliteTransmitter(location("satellite_transmitter")));
        RECEIVER = MetaTileEntities.registerMetaTileEntity(id++, new SatelliteReceiver(location("satellite_receiver")));
        for(int i = 0; i<4;i++){
            PARALLEL_TRANSMITTER = MetaTileEntities.registerMetaTileEntity(id++, new ParallelHatch(location("parallel_transmitter_" + (int)Math.pow(4, i+1) + "p"), 5 + i, true));
        }
        for(int i = 0; i<4;i++) {
            PARALLEL_RECEIVER = MetaTileEntities.registerMetaTileEntity(id++, new ParallelHatch(location("parallel_receiver_" + (int) Math.pow(4, i+1) + "p"), 5 + i, false));
        }
        PARALLEL_RACK = MetaTileEntities.registerMetaTileEntity(id++, new ParallelComputerRack(location("parallel_rack")));

        //CONTROLLERS
        SINGLE_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new SingleTank(location("fluid_tank.single")));
        MULTI_FLUID_MULTI_TANK = MetaTileEntities.registerMetaTileEntity(id++, new MultiTank(location("fluid_tank.multi")));
        GREENHOUSE = MetaTileEntities.registerMetaTileEntity(id++, new Greenhouse(location("greenhouse")));
        PYROLYSE_OVEN = MetaTileEntities.registerMetaTileEntity(1004, new PyrolyseOven(location("pyrolyse_oven")));
        SAWMILL = MetaTileEntities.registerMetaTileEntity(id++, new Sawmill(location("sawmill")));
        SATELLITE = MetaTileEntities.registerMetaTileEntity(id++, new Satellite(location("satellite")));
        PARALLEL_COMPUTER = MetaTileEntities.registerMetaTileEntity(id++, new ParallelComputer(location("parallel_computer")));
        COMMUNICATION_TOWER = MetaTileEntities.registerMetaTileEntity(id++, new CommunicationTower(location("communication_tower")));
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