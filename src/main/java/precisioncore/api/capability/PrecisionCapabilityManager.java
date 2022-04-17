package precisioncore.api.capability;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.SimpleCapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityInject;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;

public class PrecisionCapabilityManager {

    public static void init(){
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IParallelHatch.class);
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IAddresable.class);
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IParallelMultiblock.class);
    }
}
