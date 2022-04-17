package precisioncore.api.capability;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.GregtechTileCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import precisioncore.api.capability.IParallelHatch;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;

@Mod.EventBusSubscriber(modid = GTValues.MODID)
public class PrecisionCapabilities {

    @CapabilityInject(IParallelHatch.class)
    public static Capability<IParallelHatch> CAPABILITY_PARALLEL = null;

    @CapabilityInject(IAddresable.class)
    public static Capability<IAddresable> CAPABILITY_ADDRESSABLE = null;

    @CapabilityInject(IParallelMultiblock.class)
    public static Capability<IParallelMultiblock> CAPABILITY_PRECISION_CONTROLLER = null;
}
