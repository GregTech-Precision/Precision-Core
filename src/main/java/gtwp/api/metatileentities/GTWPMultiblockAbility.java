package gtwp.api.metatileentities;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gtwp.api.capability.IAddresable;
import gtwp.api.capability.IParallelHatch;

public class GTWPMultiblockAbility {

    public static final MultiblockAbility<IAddresable> SATELLITE_RECEIVER = new MultiblockAbility<>("satellite_receiver");

    public static final MultiblockAbility<IAddresable> SATELLITE_TRANSMITTER = new MultiblockAbility<>("satellite_transmitter");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_IN = new MultiblockAbility<>("parallel_hatch_in");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_OUT = new MultiblockAbility<>("parallel_hatch_out");

}
