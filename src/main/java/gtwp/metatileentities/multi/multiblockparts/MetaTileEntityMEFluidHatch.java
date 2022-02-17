/*package gtwp.metatileentities.multi.multiblockparts;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import gregtech.api.capability.impl.FluidTankList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class MetaTileEntityMEFluidHatch extends MetaTileEntityIOHatch {

    MachineSource source;
    AENetworkProxy proxy;

    public MetaTileEntityMEFluidHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        source = new MachineSource(this.getHolder());
    }

    @Override
    public AENetworkProxy getProxy() {
        if(proxy == null)
        {
            proxy = new AENetworkProxy(getHolder(), "proxy", getStackForm(1), true);
            proxy.onReady();
            proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        }
        return proxy;
    }

    @Override
    public void update() {
        super.update();
        if(getOffsetTimer() % 8 == 0)
            storeItemInME();
    }

    public void storeItemInME()
    {
        IItemList<IAEFluidStack> fluid = AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class).createList();
        for(int i = 0; i<exportFluids.getFluidTanks().size();i++){
            fluid.add(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class).createStack(exportFluids.getTankAt(i).getFluid()));
        }

        Platform.postListChanges(fluid, fluid, this.getHolder(), source);
    }
}*/
