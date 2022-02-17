package gtwp.metatileentities.multi.multiblockparts;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.parts.FluidHandlerAdapter;
import appeng.fluids.util.AEFluidInventory;
import appeng.fluids.util.IAEFluidInventory;
import appeng.fluids.util.IAEFluidTank;
import appeng.me.GridAccessException;
import appeng.me.GridException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MEMonitorHandler;
import appeng.me.helpers.MachineSource;
import appeng.me.storage.MEInventoryHandler;
import appeng.util.Platform;
import gregtech.api.capability.impl.FluidTankList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class MetaTileEntityMEFluidHatch extends MetaTileEntityIOHatch implements IMEMonitorHandlerReceiver<IAEFluidStack>, IAEFluidInventory {

    MachineSource source;
    AENetworkProxy proxy;
    MEInventoryHandler<IAEFluidStack> handler;
    AEFluidInventory config = new AEFluidInventory(this, 25);

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
            storeFluidInME();
    }

    public void storeFluidInME() {
        return;
    }

    public void resetCache()
    {
        final IMEInventory<IAEFluidStack> in = this.getFluidHandler();
        IItemList<IAEFluidStack> before = AEApi.instance().storage().getStorageChannel( IFluidStorageChannel.class ).createList();
        if( in != null )
            before = in.getAvailableItems(before);

        final IMEInventory<IAEFluidStack> out = this.getFluidHandler();
        if(in != out) {
            IItemList<IAEFluidStack> after = AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class).createList();
            if (out != null)
                after = in.getAvailableItems(after);

            Platform.postListChanges(before, after, this, source);
        }
    }

    public IMEInventory<IAEFluidStack> getFluidWrapper()
    {
        //return new FluidHandlerAdapter(getFluidInventory(), getProxy());
        return null;
    }

    public MEInventoryHandler<IAEFluidStack> getFluidHandler()
    {
        IMEInventory<IAEFluidStack> inventory = getFluidWrapper();
        handler = new MEInventoryHandler<>(inventory, AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
        return handler;
    }

    @Override
    public boolean isValid(Object o) {
        return handler == o;
    }

    @Override
    public void postChange(IBaseMonitor<IAEFluidStack> iBaseMonitor, Iterable<IAEFluidStack> iterable, IActionSource iActionSource) {
        try {
            if (getProxy().isActive())
                getProxy().getStorage().postAlterationOfStoredItems(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class), iterable, source);
        }
        catch (GridAccessException e)
        {

        }
    }

    @Override
    public void onListUpdate() {

    }

    @Override
    public void onFluidInventoryChanged(IAEFluidTank iaeFluidTank, int i) {
        if(iaeFluidTank == config) resetCache();
    }
}
