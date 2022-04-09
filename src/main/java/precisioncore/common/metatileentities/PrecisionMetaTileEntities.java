package precisioncore.common.metatileentities;

import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.MetaTileEntities;
import precisioncore.PrecisionCore;
import precisioncore.common.metatileentities.multi.matrixsystem.*;
import precisioncore.common.metatileentities.multi.parallel.*;
import precisioncore.common.metatileentities.multi.processing.Greenhouse;
import precisioncore.common.metatileentities.multi.processing.Sawmill;
import precisioncore.common.metatileentities.multi.tanks.MultiTank;
import precisioncore.common.metatileentities.multi.processing.PyrolyseOven;
import precisioncore.common.metatileentities.multi.tanks.SingleTank;
import precisioncore.common.metatileentities.multi.tanks.IOHatch;
import precisioncore.common.metatileentities.multi.multiblockparts.MEItemOutputHatch;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class PrecisionMetaTileEntities {

    private static int id = 11000;

    //HATCHES
    public static IOHatch IO_HATCH;
    public static MEItemOutputHatch ME_HATCH;
    public static SatelliteHatch SATELLITE_TRANSMITTER;
    public static SatelliteHatch SATELLITE_RECEIVER;
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

    //MATRIX SYSTEM
    public static MEConnectorHatch ME_CONNECTOR;
    public static MESystemProvider ME_SYSTEM_PROVIDER;
    public static MatrixParticleDiffuser PARAMETRIC_DIFFUSER;
    public static MatrixParticleStabilizer PARAMETRIC_STABILIZER;
    public static MatrixParticleContainment PARAMETRIC_CONTAINMENT;

    public static void init() {
        GTLog.logger.info("Precision Core Registering New Meta Tile Entities");

        //HATCHES
        IO_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new IOHatch(location("fluid_hatch.io")));

        ME_HATCH = MetaTileEntities.registerMetaTileEntity(id++, new MEItemOutputHatch(location("item_hatch.me")));
        SATELLITE_TRANSMITTER = MetaTileEntities.registerMetaTileEntity(id++, new SatelliteHatch(location("satellite_transmitter"), true));
        SATELLITE_RECEIVER = MetaTileEntities.registerMetaTileEntity(id++, new SatelliteHatch(location("satellite_receiver"), false));
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
        PYROLYSE_OVEN = MetaTileEntities.registerMetaTileEntity(1004, new PyrolyseOven(location("pyrolyse_oven"))); // GTCEu pyrolyse oven id is 1004
        SAWMILL = MetaTileEntities.registerMetaTileEntity(id++, new Sawmill(location("sawmill")));
        SATELLITE = MetaTileEntities.registerMetaTileEntity(id++, new Satellite(location("satellite")));
        PARALLEL_COMPUTER = MetaTileEntities.registerMetaTileEntity(id++, new ParallelComputer(location("parallel_computer")));
        COMMUNICATION_TOWER = MetaTileEntities.registerMetaTileEntity(id++, new CommunicationTower(location("communication_tower")));
        //MATRIX SYSTEM
        ME_CONNECTOR = MetaTileEntities.registerMetaTileEntity(id++, new MEConnectorHatch(location("me_connector")));
        ME_SYSTEM_PROVIDER = MetaTileEntities.registerMetaTileEntity(id++, new MESystemProvider(location("me_system_provider")));
        PARAMETRIC_DIFFUSER = MetaTileEntities.registerMetaTileEntity(id++, new MatrixParticleDiffuser(location("parametric_diffuser")));
        PARAMETRIC_STABILIZER = MetaTileEntities.registerMetaTileEntity(id++, new MatrixParticleStabilizer(location("parametric_stabilizer")));
        PARAMETRIC_CONTAINMENT = MetaTileEntities.registerMetaTileEntity(id++, new MatrixParticleContainment(location("parametric_containment")));
    }

    private static void registerSimpleMetaTileEntity(SimpleMachineMetaTileEntity[] machines,
                                                     int startId,
                                                     String name,
                                                     RecipeMap<?> map,
                                                     ICubeRenderer texture,
                                                     boolean hasFrontFacing,
                                                     Function<Integer, Integer> tankScalingFunction) {
        MetaTileEntities.registerSimpleMetaTileEntity(machines, startId, name, map, texture, hasFrontFacing, PrecisionMetaTileEntities::location, tankScalingFunction);
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
        return new ResourceLocation(PrecisionCore.MODID, name);
    }
}