package precisioncore.common.metatileentities.multi.matrixsystem;

import appeng.api.networking.GridFlags;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.metatileentities.PrecisionMetaTileEntities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class MEConnectorHatch extends MetaTileEntityMultiblockPart {

    private AENetworkProxy proxy;
    private IActionSource source;
    private boolean isProxyActive;

    public MEConnectorHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 5);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MEConnectorHatch(metaTileEntityId);
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
                writeCustomData(PrecisionDataCodes.RENDER_STATE_UPDATE, b -> b.writeBoolean(isProxyActive));
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
        (isProxyActive ? PrecisionTextures.ME_CONNECTOR_ACTIVE : PrecisionTextures.ME_CONNECTOR_INACTIVE).renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.RENDER_STATE_UPDATE){
            isProxyActive = buf.readBoolean();
            scheduleRenderUpdate();
        }
    }
}
