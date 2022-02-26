package gtwp.metatileentities.multi.multiblockparts;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartHost;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AECableVariant;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.block.AEBaseTileBlock;
import appeng.entity.AEBaseEntityItem;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import appeng.parts.AEBasePart;
import appeng.parts.networking.PartCable;
import appeng.parts.networking.PartCableSmart;
import appeng.tile.AEBaseTile;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.networking.TileCableBus;
import appeng.util.Platform;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityItemBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityMEItemOutputHatch extends MetaTileEntityItemBus {

    private IActionSource source;
    private AENetworkProxy proxy;

    public MetaTileEntityMEItemOutputHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityMEItemOutputHatch(metaTileEntityId);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        getProxy();
    }

    @Override
    public void update() {
        if(getOffsetTimer() % 5 == 0)
            pushItemsIntoME();
    }

    private IActionSource getRequest() {
        if (source == null)
            source = new MachineSource((IActionHost)this.getHolder());
        return source;
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull AEPartLocation aePartLocation) {
        return aePartLocation.getFacing().equals(getFrontFacing()) ? AECableType.SMART : AECableType.NONE;
    }

    @Override
    public AENetworkProxy getProxy() {
        if (proxy == null) {
            proxy = new AENetworkProxy((IGridProxyable) this.getHolder(), "proxy", this.getStackForm(1), true);
            proxy.onReady();
            proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        }
        return proxy;
    }

    @Override
    public void gridChanged() {

    }

    private void pushItemsIntoME()
    {
        AENetworkProxy px = getProxy();
        if (px == null) return;
        IItemList<IAEItemStack> items = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        for(int i = 0; i<exportItems.getSlots();i++) {
            items.add(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(exportItems.getStackInSlot(i)));
            exportItems.setStackInSlot(0, ItemStack.EMPTY);
        }
        try {
            IMEMonitor<IAEItemStack> sg = px.getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            for (IAEItemStack s: items ) {
                if (s.getStackSize() == 0) continue;
                IAEItemStack rest = Platform.poweredInsert(px.getEnergy(), sg, s, getRequest());
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