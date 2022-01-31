package gtwp.metatileentities.multi.multiblockparts;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.GTFluidUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MetaTileEntityMultiIOHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IFluidHandler> {

    public MetaTileEntityMultiIOHatch(ResourceLocation metaTileEntityId)
    {
        super(metaTileEntityId, 0);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityMultiIOHatch(metaTileEntityId);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 5 == 0L && isAttachedToMultiBlock() && getFrontFacing() == EnumFacing.DOWN) {
            TileEntity tileEntity = getWorld().getTileEntity(getPos().offset(getFrontFacing()));
            IFluidHandler fluidHandler = tileEntity == null ? null : tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing().getOpposite());
            if (fluidHandler != null) {
                GTFluidUtils.transferFluids(fluidInventory, fluidHandler, Integer.MAX_VALUE);
            }
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.PIPE_OUT_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.fluidInventory = new FluidHandlerProxy(new FluidTankList(false), new FluidTankList(false));
    }

    @Override
    public void addToMultiBlock(MultiblockControllerBase controllerBase) {
        super.addToMultiBlock(controllerBase);
        this.fluidInventory = new FluidHandlerProxy(new FluidTankList(false, controllerBase.getImportFluids()), controllerBase.getExportFluids());
    }

    @Override
    public void removeFromMultiBlock(MultiblockControllerBase controllerBase) {
        super.removeFromMultiBlock(controllerBase);
        this.fluidInventory = new FluidHandlerProxy(new FluidTankList(false), new FluidTankList(false));
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        if(getController() == null)
        {
            return Textures.SOLID_STEEL_CASING;
        }
        return super.getBaseTexture();
    }

    @Override
    public int getDefaultPaintingColor() {
        return 0xFFFFFF;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public MultiblockAbility<IFluidHandler> getAbility() {
        return MultiblockAbility.TANK_VALVE;
    }

    @Override
    public void registerAbilities(List<IFluidHandler> list) {
        list.add(this.importFluids);
    }

    @Override
    protected boolean shouldSerializeInventories() {
        return false;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }


}
