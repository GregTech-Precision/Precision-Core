package precisioncore.integration.theoneprobe;

import precisioncore.integration.theoneprobe.provider.*;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbeCompatibility {

    public static void registerCompatibility() {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new ParallelProvider());
        oneProbe.registerProvider(new AddressableProvider());
        oneProbe.registerProvider(new ControllerProvider());
    }
}
