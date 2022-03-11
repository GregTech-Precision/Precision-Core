package gtwp.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.recipes.RecipeMap;
import gregtech.common.metatileentities.MetaTileEntities;
import gtwp.common.metatileentities.multi.parallel.ParallelComputer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class ParallelComputerLogic extends AbstractRecipeLogic {
    
    public ParallelComputerLogic(ParallelComputer tileEntity) {
        super(tileEntity, null);
    }

    @Override
    public ParallelComputer getMetaTileEntity() {
        return ((ParallelComputer) super.getMetaTileEntity());
    }

    @Override
    protected long getEnergyInputPerSecond() {
        return 0;
    }

    @Override
    protected long getEnergyStored() {
    }

    @Override
    protected long getEnergyCapacity() {
        return 0;
    }

    @Override
    protected boolean drawEnergy(int i, boolean b) {

        return true;
    }

    @Override
    protected long getMaxVoltage() {
        return 0;
    }

    @Override
    public void update() {
        super.update();
    }
}
