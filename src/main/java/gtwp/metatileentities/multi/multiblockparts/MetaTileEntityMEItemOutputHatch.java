package gtwp.metatileentities.multi.multiblockparts;

import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.core.api.ApiStorage;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityItemBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityMEItemOutputHatch extends MetaTileEntityItemBus implements IGridProxyable, IActionHost {

    private IActionSource source;
    private AENetworkProxy proxy;
    IItemList<IAEItemStack> items = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();

    public MetaTileEntityMEItemOutputHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityMEItemOutputHatch(metaTileEntityId);
    }

    @Override
    public void update() {
        if(getOffsetTimer() % 8 == 0) {
            for(int i = 0; i<exportItems.getSlots();i++)
                items.add(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(exportItems.getStackInSlot(i)));
            pushItemsIntoMe();
        }
    }

    private IActionSource getRequest() {
        if (source == null)
            source = new MachineSource((IActionHost)getWorld().getTileEntity(getPos()));
        return source;
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull AEPartLocation aePartLocation) {
        GTLog.logger.info("get cable connection type call");
        return aePartLocation.getFacing() == getFrontFacing() ? AECableType.SMART : AECableType.NONE;
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public AENetworkProxy getProxy() {
        if (proxy == null) {
            TileEntity te = getWorld().getTileEntity(getPos());
            if (te instanceof IGridProxyable) {
                proxy = new AENetworkProxy((IGridProxyable) te, "proxy", this.getStackForm(1), true);
                proxy.onReady();
                proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            }
            else GTLog.logger.info("not an instance of IGridProxyable");
        }
        return this.proxy;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(getWorld(), getPos());
    }

    @Override
    public void gridChanged() {

    }

    @Nullable
    @Override
    public IGridNode getGridNode(@Nonnull AEPartLocation aePartLocation) {
        AENetworkProxy np = getProxy();
        if(np == null) GTLog.logger.info("getGridNode proxy is null");
        return aePartLocation.getFacing() == getFrontFacing() && np != null ? np.getNode() : null;
    }

    @Nonnull
    @Override
    public IGridNode getActionableNode() {
        AENetworkProxy np = getProxy();
        if(np == null) GTLog.logger.info("getActionableNode proxy is null");
        return np != null ? np.getNode() : null;
    }

    private void pushItemsIntoMe()
    {
        if(items.size() == 0) return;
        AENetworkProxy p = getProxy();
        if (proxy == null) return;
        try {
            IMEMonitor<IAEItemStack> sg = p.getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            for (IAEItemStack s: items ) {
                if (s.getStackSize() == 0) continue;
                IAEItemStack rest = Platform.poweredInsert(p.getEnergy(), sg, s, getRequest());
                if (rest != null && rest.getStackSize() > 0) {
                    s.setStackSize(rest.getStackSize());
                    break;
                }
                s.setStackSize(0);
            }
        }
        catch( final GridAccessException ignored )
        {
            //exception
        }
    }

    @Override
    public String getTierlessTooltipKey() {
        return super.getTierlessTooltipKey();
    }

    @Override
    public boolean canPartShare() {
        return true;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtwp.multi.meitemoutputhatch"));
    }
}
