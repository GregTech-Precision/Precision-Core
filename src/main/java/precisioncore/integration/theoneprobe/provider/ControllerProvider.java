package precisioncore.integration.theoneprobe.provider;

import gregtech.integration.theoneprobe.provider.CapabilityInfoProvider;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.PrecisionCore;
import precisioncore.api.capability.IParallelMultiblock;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;

public class ControllerProvider extends CapabilityInfoProvider<IParallelMultiblock> {

    @Override
    protected Capability<IParallelMultiblock> getCapability() {
        return PrecisionCapabilities.CAPABILITY_PRECISION_CONTROLLER;
    }

    @Override
    protected void addProbeInfo(IParallelMultiblock controller, IProbeInfo probeInfo, TileEntity tileEntity, EnumFacing enumFacing) {
        int currentParallelUsage = ((PrecisionRecipeMapMultiblockController) controller).getRecipeMapWorkable().getCurrentParallel();
        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
        vertical.text(TextStyleClass.INFO + "{*precisioncore.top.controller.parallel*} "+currentParallelUsage);

    }

    @Override
    public String getID() {
        return PrecisionCore.MODID+"controller_provider";
    }
}
