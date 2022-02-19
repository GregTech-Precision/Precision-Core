package gtwp.metatileentities.longpipes;

import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.GTEnergyWrapper;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTFluidUtils;
import gtwp.blocks.BlockPipeline;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class PipelineFluid extends PipelineBase {

    public PipelineFluid(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    /*@Override
    protected void initializeInventory() {
        this.importFluids = new FluidTankList(true, new FluidTank)
        this.exportFluids = this.importFluids;
        this.fluidInventory = new FluidHandlerProxy(importFluids, exportFluids);
    }

    public List<FluidTank> makeFluidTank(){

    }*/

    @Override
    public void update() {
        super.update();
        if(getOffsetTimer() % 20 == 8) {
            if(isInput() && getPair() != null){
                IFluidHandler fromHandler = null, toHandler = null;
                if(!getWorld().isAirBlock(getPos().offset(getFrontFacing())))
                    fromHandler = getWorld().getTileEntity(getPos().offset(getFrontFacing())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing().getOpposite());
                if(!getWorld().isAirBlock(getPair().getPos().offset(getFrontFacing().getOpposite())))
                    toHandler = getWorld().getTileEntity(getPair().getPos().offset(getFrontFacing().getOpposite())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing());
                if(fromHandler != null && toHandler != null)
                    GTFluidUtils.transferFluids(fromHandler, toHandler, Integer.MAX_VALUE);
            }
        }
    }

    @Override
    public int getPipeType() {
        return 0;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineFluid(metaTileEntityId);
    }
}
