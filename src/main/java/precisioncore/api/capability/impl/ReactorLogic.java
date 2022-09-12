package precisioncore.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.unification.PrecisionMaterials;
import precisioncore.common.metatileentities.multi.nuclear.Reactor;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactorLogic extends AbstractRecipeLogic {

    private static final int STEAM_PER_WATER = 160;

    private final int maxHeat;
    private int currentHeat = 0;
    private int waterToConsume = 0;
    private float rodLevel;
    private float rodModifier;
    private boolean isMOX;
    private int heatPerSecond;


    /**
     * Produces steam equals energy reactor tier * 10A
     */
    public ReactorLogic(Reactor reactor, int tier){
        super(reactor, null);
        this.maxHeat = (int) GTValues.V[tier-1];
        this.workingEnabled = false;
    }

    @Override
    public Reactor getMetaTileEntity() {
        return (Reactor) super.getMetaTileEntity();
    }

    public void onRodChanges(){
        List<IReactorHatch> reactorHatchList = getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH);
        float rodCount = reactorHatchList.size();
        rodLevel = reactorHatchList.stream().mapToInt(IReactorHatch::getRodLevel).sum() / rodCount / 10;
        rodModifier = reactorHatchList.stream().mapToInt(IReactorHatch::getRodModifier).sum() / rodCount / 16;
        isMOX = reactorHatchList.stream().allMatch(rodHatch -> !rodHatch.hasRod() || rodHatch.isMOX());
        heatPerSecond = (int) (10 * Math.min(1, rodModifier * 2));
    }

    @Override
    public void update() {
        if ((!getMetaTileEntity().isActive() || !isWorkingEnabled()) && currentHeat > 0) {
            setHeat(currentHeat - 1);
        }
        super.update();
    }

    @Override
    protected void trySearchNewRecipe() {
        if (rodLevel * rodModifier > 0) {
            setMaxProgress(20);
            this.progressTime = 1;
            this.waterToConsume = getCurrentWaterConsumption();
            if (wasActiveAndNeedsUpdate) {
                wasActiveAndNeedsUpdate = false;
            } else {
                setActive(true);
            }
            metaTileEntity.getNotifiedFluidInputList().clear();
            metaTileEntity.getNotifiedFluidOutputList().clear();
        }
    }

    @Override
    protected void updateRecipeProgress() {
        if (consumeWater(waterToConsume, false)) {
            consumeWater(waterToConsume, true);
            outputSteam(waterToConsume * STEAM_PER_WATER);
        } else if (waterToConsume > 10) {
            getMetaTileEntity().doExplosion(1000 * getCurrentHeatPercentage());
        }

        if (++progressTime > maxProgressTime) {
            completeRecipe();
        }
    }

    @Override
    protected void completeRecipe() {
        if (currentHeat < maxHeat) {
            setHeat(currentHeat + heatPerSecond);
        }
        depleteUraniumFuel();
        progressTime = 0;
        waterToConsume = 0;
        setMaxProgress(0);
        wasActiveAndNeedsUpdate = true;
    }

    public float getCurrentHeatPercentage(){
        return Math.min(rodLevel, ((float) currentHeat / (float) maxHeat));
    }

    public int getCurrentWaterConsumption(){
        return (int) (maxHeat * getCurrentHeatPercentage() * rodModifier);
    }

    public int getCurrentSteamProduction(){
        return waterToConsume * STEAM_PER_WATER;
    }

    private void setHeat(int heat){
        if(this.currentHeat != heat && !getMetaTileEntity().getWorld().isRemote){
            writeCustomData(PrecisionDataCodes.HEAT_UPDATE, buf -> buf.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    private boolean consumeWater(int amount, boolean drain){
        FluidStack fluid = getInputTank().drain(Materials.Water.getFluid(amount), drain);
        if(fluid == null || fluid.amount == 0)
            return false;
        return fluid.amount == amount;
    }

    private void depleteUraniumFuel(){
        getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).forEach(IReactorHatch::depleteRod);
    }

    private void outputSteam(int amount){
        getOutputTank().fill((isMOX ? PrecisionMaterials.SuperHeatedSteam : Materials.Steam).getFluid(amount), true);
    }

    @Override
    public void writeInitialData(@Nonnull PacketBuffer buf) {
        super.writeInitialData(buf);
        buf.writeVarInt(this.currentHeat);
    }

    @Override
    public void receiveInitialData(@Nonnull PacketBuffer buf) {
        super.receiveInitialData(buf);
        this.currentHeat = buf.readVarInt();
    }

    @Override
    public void receiveCustomData(int dataId, @Nonnull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.HEAT_UPDATE){
            this.currentHeat = buf.readVarInt();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("workingEnabled", this.workingEnabled);
        tag.setInteger("progressTime", this.progressTime);
        tag.setInteger("maxProgressTime", this.maxProgressTime);
        tag.setInteger("currentHeat", this.currentHeat);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        this.workingEnabled = compound.getBoolean("workingEnabled");
        this.progressTime = compound.getInteger("progressTime");
        this.maxProgressTime = compound.getInteger("maxProgressTime");
        this.currentHeat = compound.getInteger("currentHeat");
    }

    @Override
    protected long getEnergyInputPerSecond() {
        GTLog.logger.error("Large Boiler called getEnergyInputPerSecond(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        GTLog.logger.error("Large Boiler called getEnergyStored(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyCapacity() {
        GTLog.logger.error("Large Boiler called getEnergyCapacity(), this should not be possible!");
        return 0;
    }

    @Override
    protected boolean drawEnergy(int i, boolean b) {
        GTLog.logger.error("Large Boiler called drawEnergy(), this should not be possible!");
        return false;
    }

    @Override
    protected long getMaxVoltage() {
        GTLog.logger.error("Large Boiler called getMaxVoltage(), this should not be possible!");
        return 0;
    }
}
