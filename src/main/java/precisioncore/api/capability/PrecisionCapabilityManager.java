package precisioncore.api.capability;

import gregtech.api.capability.SimpleCapabilityManager;

public class PrecisionCapabilityManager {

    public static void init(){
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IParallelHatch.class);
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IAddresable.class);
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IParallelMultiblock.class);
        SimpleCapabilityManager.registerCapabilityWithNoDefault(IReactorHatch.class);
    }
}
