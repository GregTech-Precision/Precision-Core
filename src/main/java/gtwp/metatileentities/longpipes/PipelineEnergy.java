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

    public PipelineEnergy(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public boolean checkPipeType(BlockPipeline pipe, BlockPos location) {
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (isInput() && getPair() != null) {
            if (getInput() != null && getPair().getOutput() != null) {
                IEnergyContainer fromHandler = getInput().getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, getFrontFacing().getOpposite());
                IEnergyContainer toHandler = getPair().getOutput().getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, getPair().getFrontFacing());
                if (fromHandler != null && toHandler != null) {
                    long accepted = toHandler.acceptEnergyFromNetwork(getPair().getFrontFacing().getOpposite(), fromHandler.getOutputVoltage(), fromHandler.getOutputVoltage());
                    fromHandler.removeEnergy(accepted);
                    EnergyContainerHandler.emitterContainer()
                }
            }
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PipelineEnergy(metaTileEntityId);
    }
}
