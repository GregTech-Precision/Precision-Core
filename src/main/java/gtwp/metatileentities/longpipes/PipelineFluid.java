package gtwp.metatileentities.longpipes;

import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.GTEnergyWrapper;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.pipenet.tile.TileEntityPipeBase;
import gregtech.api.util.GTFluidUtils;
import gregtech.api.util.GTLog;
import gregtech.common.pipelike.fluidpipe.tile.TileEntityFluidPipeTickable;
import gtwp.blocks.BlockPipeline;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
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

    @Override
    public void update() {
        super.update();
        if(getOffsetTimer() % 20 == 8) {
            if(isInput() && getPair() != null){
                IFluidHandler fromHandler = null, toHandler = null;
                if(getInput() != null && getPair().getOutput() != null) {
                    fromHandler = getInput().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing().getOpposite());
                    toHandler = getPair().getOutput().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getPair().getFrontFacing());
                    if(fromHandler != null && toHandler != null)
                        GTFluidUtils.transferFluids(fromHandler, toHandler, Integer.MAX_VALUE);
                }
            }
        }
    }

    @Override
    public boolean checkPipeType(BlockPipeline pipe, BlockPos location) {
        return pipe.getState(getWorld().getBlockState(location)).getPipeType() == 0;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineFluid(metaTileEntityId);
    }
}
