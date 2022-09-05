package precisioncore.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTLog;
import gregtech.common.ConfigHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.common.metatileentities.multi.nuclear.Reactor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static gregtech.api.capability.GregtechDataCodes.*;

public class ReactorRecipeLogic extends AbstractRecipeLogic {

    private static final long STEAM_PER_WATER = 160;

    private static final int FLUID_DRAIN_MULTIPLIER = 100;
    private static final int FLUID_BURNTIME_TO_EU = 800 / FLUID_DRAIN_MULTIPLIER;

    private int currentHeat;
    private int lastTickSteamOutput;
    private int excessWater, excessFuel, excessProjectedEU;

    public ReactorRecipeLogic(Reactor tileEntity) {
        super(tileEntity, null);
        this.fluidOutputs = Collections.emptyList();
        this.maxProgressTime = 8;
    }

    @Override
    public void update() {
        if ((!isActive() || !isWorkingEnabled()) && currentHeat > 0) {
            setHeat(currentHeat - 1);
        }
        super.update();
    }

    @Override
    protected void trySearchNewRecipe() {
        Reactor reactor = getMetaTileEntity();
        if (ConfigHolder.machines.enableMaintenance && !reactor.hasMaintenanceProblems()) {
            return;
        }

        // can optimize with an override of checkPreviousRecipe() and a check here

        IMultipleTankHandler inputFluids = getMetaTileEntity().getImportFluids();
        List<IReactorHatch> fuelHatches = getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH);

//        if (didStartRecipe) {
//            this.progressTime = 1;
//            this.recipeEUt = 0;
//            if (wasActiveAndNeedsUpdate) {
//                wasActiveAndNeedsUpdate = false;
//            } else {
//                setActive(true);
//            }
//        }
        metaTileEntity.getNotifiedItemInputList().clear();
        metaTileEntity.getNotifiedFluidInputList().clear();
    }

    @Override
    protected void updateRecipeProgress() {
        if (++progressTime > maxProgressTime) {
            completeRecipe();
        }
    }

    private int getMaximumHeat() {
        return 100000;
    }

    public int getHeatScaled() {
        return (int) Math.round(currentHeat / (1.0 * getMaximumHeat()) * 100);
    }

    public void setHeat(int heat) {
        if (heat != this.currentHeat && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_HEAT, b -> b.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    public int getLastTickSteam() {
        return lastTickSteamOutput;
    }

    public void setLastTickSteam(int lastTickSteamOutput) {
        if (lastTickSteamOutput != this.lastTickSteamOutput && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_LAST_TICK_STEAM, b -> b.writeVarInt(lastTickSteamOutput));
        }
        this.lastTickSteamOutput = lastTickSteamOutput;
    }

    public void invalidate() {
        progressTime = 0;
        recipeEUt = 0;
        setActive(false);
        setLastTickSteam(0);
    }

    @Override
    protected void completeRecipe() {
        progressTime = 0;
        recipeEUt = 0;
        wasActiveAndNeedsUpdate = true;
    }

    @Override
    public Reactor getMetaTileEntity() {
        return (Reactor) super.getMetaTileEntity();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("Heat", currentHeat);
        compound.setInteger("ExcessFuel", excessFuel);
        compound.setInteger("ExcessWater", excessWater);
        compound.setInteger("ExcessProjectedEU", excessProjectedEU);
        return compound;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        this.currentHeat = compound.getInteger("Heat");
        this.excessFuel = compound.getInteger("ExcessFuel");
        this.excessWater = compound.getInteger("ExcessWater");
        this.excessProjectedEU = compound.getInteger("ExcessProjectedEU");
    }

    @Override
    public void writeInitialData(@Nonnull PacketBuffer buf) {
        super.writeInitialData(buf);
        buf.writeVarInt(currentHeat);
        buf.writeVarInt(lastTickSteamOutput);
    }

    @Override
    public void receiveInitialData(@Nonnull PacketBuffer buf) {
        super.receiveInitialData(buf);
        this.currentHeat = buf.readVarInt();
        this.lastTickSteamOutput = buf.readVarInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == BOILER_HEAT) {
            this.currentHeat = buf.readVarInt();
        } else if (dataId == BOILER_LAST_TICK_STEAM) {
            this.lastTickSteamOutput = buf.readVarInt();
        }
    }

    // Required overrides to use RecipeLogic, but all of them are redirected by the above overrides.

    @Override
    protected long getEnergyInputPerSecond() {
        GTLog.logger.error("Large reactor called getEnergyInputPerSecond(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        GTLog.logger.error("Large reactor called getEnergyStored(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyCapacity() {
        GTLog.logger.error("Large reactor called getEnergyCapacity(), this should not be possible!");
        return 0;
    }

    @Override
    protected boolean drawEnergy(int recipeEUt, boolean simulate) {
        GTLog.logger.error("Large reactor called drawEnergy(), this should not be possible!");
        return false;
    }

    @Override
    protected long getMaxVoltage() {
        GTLog.logger.error("Large reactor called getMaxVoltage(), this should not be possible!");
        return 0;
    }
}
