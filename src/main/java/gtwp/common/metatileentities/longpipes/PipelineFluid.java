package gtwp.common.metatileentities.longpipes;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTFluidUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class PipelineFluid extends PipelineBase {

    public PipelineFluid(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public void update() {
        super.update();
        if(getOffsetTimer() % 5 == 0) {
            if(isInput() && getPair() != null){
                if(getInput() != null && getPair().getOutput() != null) {
                    IFluidHandler fromHandler = getInput().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing().getOpposite());
                    if(fromHandler == null) return;
                    IFluidHandler toHandler = getPair().getOutput().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getPair().getFrontFacing());
                    if(toHandler == null) return;

                    GTFluidUtils.transferFluids(fromHandler, toHandler, Integer.MAX_VALUE);
                }
            }
        }
    }

    @Override
    public int getPipeMeta() {
        return 0;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineFluid(metaTileEntityId);
    }
}
