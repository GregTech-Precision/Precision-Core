package gtwp.common.metatileentities.multi.matrixsystem;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class AEConnectorHatch extends MetaTileEntityMultiblockPart {

    private AENetworkProxy proxy;
    private IActionSource source;

    public AEConnectorHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new AEConnectorHatch(metaTileEntityId);
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
    public void onFirstTick() {
        super.onFirstTick();
        getProxy();
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
    public boolean isActive() {
        return proxy.isActive();
    }

    @Override
    public void gridChanged() {

    }
}
