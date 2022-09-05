package precisioncore.common.metatileentities.multi.nuclear;

import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class ReactorSteamHatch extends MetaTileEntityFluidHatch {

    public ReactorSteamHatch(ResourceLocation metaTileEntityId, boolean isExportHatch) {
        super(metaTileEntityId, 4, isExportHatch);
    }
}
