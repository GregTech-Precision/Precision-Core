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
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.render.GTWPTextures;
import gtwp.common.metatileentities.GTWPMetaTileEntities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class AEConnectorHatch extends MetaTileEntityMultiblockPart {

    private AENetworkProxy proxy;
    private IActionSource source;
    private boolean isProxyActive;

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
            isProxyActive = proxy.isActive();
        }
        return proxy;
    }

    @Override
    public boolean isActive() {
        return getProxy().isActive();
    }

    @Override
    public void update() {
        super.update();
        if(getWorld().isRemote) return;

        if(getOffsetTimer() % 10 == 0){
            boolean newProxyActive = updateRenderState();
            if(newProxyActive != isProxyActive){
                isProxyActive = newProxyActive;
                writeCustomData(GTWPDataCodes.RENDER_STATE_UPDATE, b -> b.writeBoolean(isProxyActive));
            }
        }
    }

    private boolean updateRenderState(){
        if(!getWorld().isRemote){
            return getProxy().isActive();
        }
        return false;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        (isProxyActive ? GTWPTextures.AE_CONNECTOR_ACTIVE : GTWPTextures.AE_CONNECTOR_INACTIVE).renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.RENDER_STATE_UPDATE){
            isProxyActive = buf.readBoolean();
            scheduleRenderUpdate();
        }
    }
}
