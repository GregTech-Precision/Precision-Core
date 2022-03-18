package gtwp.api.metatileentities;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.IReceiver;
import gtwp.api.capability.ITransmitter;

public class GTWPMultiblockAbility {
    public static final MultiblockAbility<IReceiver> RECEIVER = new MultiblockAbility<>("receiver");

    public static final MultiblockAbility<ITransmitter> TRANSMITTER = new MultiblockAbility<>("transmitter");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_IN = new MultiblockAbility<>("parallel_hatch_in");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_OUT = new MultiblockAbility<>("parallel_hatch_out");

}
