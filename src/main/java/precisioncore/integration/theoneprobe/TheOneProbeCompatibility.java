package precisioncore.integration.theoneprobe;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import precisioncore.integration.theoneprobe.provider.AddressableProvider;
import precisioncore.integration.theoneprobe.provider.ControllerProvider;
import precisioncore.integration.theoneprobe.provider.ParallelProvider;
import precisioncore.integration.theoneprobe.provider.RodProvider;

public class TheOneProbeCompatibility {

    public static void registerCompatibility() {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new ParallelProvider());
        oneProbe.registerProvider(new AddressableProvider());
        oneProbe.registerProvider(new ControllerProvider());
        oneProbe.registerProvider(new RodProvider());
    }
}
