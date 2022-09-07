package precisioncore.api.metatileentities;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.capability.IParallelHatch;
import precisioncore.api.capability.IReactorHatch;

public class PrecisionMultiblockAbility {

    public static final MultiblockAbility<IAddresable> SATELLITE_RECEIVER = new MultiblockAbility<>("satellite_receiver");

    public static final MultiblockAbility<IAddresable> SATELLITE_TRANSMITTER = new MultiblockAbility<>("satellite_transmitter");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_IN = new MultiblockAbility<>("parallel_hatch_in");

    public static final MultiblockAbility<IParallelHatch> PARALLEL_HATCH_OUT = new MultiblockAbility<>("parallel_hatch_out");

    public static final MultiblockAbility<IReactorHatch> REACTOR_HATCH = new MultiblockAbility<>("reactor_hatch");
}
