package gtwpp.machines.multiblocks;

import gregtech.api.capability.impl.FluidTankList;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityFluidHatch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;

public class MetaTileEntityMultipleFluidHatch extends MetaTileEntityFluidHatch
{
    private FluidTank fluidTanks[];

    public MetaTileEntityMultipleFluidHatch(ResourceLocation resloc, int size)
    {
        super(resloc, 4, false);

        for(int i = 0; i<size;i++)
        {
            fluidTanks[i] = new FluidTank(32000);
        }
        initializeInventory();
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, fluidTanks);
    }
}
