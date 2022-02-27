package gtwp.metatileentities.longpipes;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.cover.ICoverable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTLog;
import gregtech.common.pipelike.cable.net.EnergyNetHandler;
import gregtech.common.pipelike.cable.tile.TileEntityCable;
import gtwp.blocks.BlockPipeline;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class PipelineEnergy extends PipelineBase{

    private final int tier;
    public PipelineEnergy(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId);
        this.tier = tier;
    }

    @Override
    public int getPipeMeta() {
        return 1+getTier();
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void update() {
        super.update();
        if (isInput() && getPair() != null) {
            if (getInput() != null && getPair().getOutput() != null) {
                IEnergyContainer fromHandler = getInput().getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, getFrontFacing().getOpposite());
                if(fromHandler == null) return;
                IEnergyContainer toHandler = getPair().getOutput().getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, getPair().getFrontFacing());
                if(toHandler == null) return;

                if(toHandler.acceptEnergyFromNetwork(getPair().getFrontFacing().getOpposite(), fromHandler.getOutputVoltage(), fromHandler.getOutputVoltage()) > 0)
                    fromHandler.removeEnergy(fromHandler.getOutputVoltage()*fromHandler.getOutputAmperage());
            }
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineEnergy(metaTileEntityId, getTier());
    }
}
