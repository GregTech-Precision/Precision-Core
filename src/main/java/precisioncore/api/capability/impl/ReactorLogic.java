package precisioncore.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.unification.material.Materials;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.common.metatileentities.multi.nuclear.Reactor;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactorLogic extends AbstractRecipeLogic {

    private static final int STEAM_PER_WATER = 40;
    private static final int DATA_HEAT = 989;

    private final int maxHeat;
    private int currentHeat = 0;

    private boolean lastIsMOX = false;
    private int waterToConsume = 0;
    private float lastRodLevel = 0;


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

    @Override
    public void update() {
        if ((!getMetaTileEntity().isActive() || !isWorkingEnabled()) && currentHeat > 0) {
            setHeat(currentHeat - 1);
        }
        super.update();
    }

    @Override
    protected void trySearchNewRecipe() {
        if (getRodLevelPercentage() > 0) {
            setMaxProgress(20);
            this.progressTime = 1;
            this.lastIsMOX = isMOX();
            this.lastRodLevel = getRodLevelPercentage();
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
        } else if (waterToConsume != 0) {
            getMetaTileEntity().doExplosion(1000 * getCurrentHeatPercentage());
        }

        if (++progressTime > maxProgressTime) {
            if (currentHeat < maxHeat) {
                setHeat(currentHeat + (int) Math.max(1, getRodAmount() * getRodLevelPercentage()));
            }
            completeRecipe();
        }
    }

    @Override
    protected void completeRecipe() {
        progressTime = 0;
        lastIsMOX = false;
        waterToConsume = 0;
        lastRodLevel = 0;
        setMaxProgress(0);
        wasActiveAndNeedsUpdate = true;
    }

    public float getRodLevelPercentage(){
        List<IReactorHatch> reactorHatchList = getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH);
        float maxRodLevel = reactorHatchList.size() * 10;
        float currentLevel = reactorHatchList.stream().mapToInt(IReactorHatch::getRodLevel).sum();
        return currentLevel / maxRodLevel;
    }

    private int getRodAmount(){
        return (int) getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).size();
    }

    public boolean isMOX(){
        return getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).stream().allMatch(IReactorHatch::isMOX);
    }

    public float getCurrentHeatPercentage(){
        return Math.min(lastRodLevel, ((float) currentHeat / (float) maxHeat));
    }

    public int getCurrentWaterConsumption(){
        return (int) (maxHeat * getCurrentHeatPercentage() * getFuelModifier());
    }

    public int getCurrentSteamProduction(){
        return waterToConsume * STEAM_PER_WATER;
    }

    private int getFuelModifier(){
        return lastIsMOX ? 4 : 1;
    }

    private void setHeat(int heat){
        if(this.currentHeat != heat && !getMetaTileEntity().getWorld().isRemote){
            writeCustomData(DATA_HEAT, buf -> buf.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    private boolean consumeWater(int amount, boolean drain){
        FluidStack fluid = getInputTank().drain(Materials.Water.getFluid(amount), drain);
        if(fluid == null || fluid.amount == 0)
            return false;
        return fluid.amount == amount;
    }

    private void outputSteam(int amount){
        getOutputTank().fill(Materials.Steam.getFluid(amount), true);
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
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == DATA_HEAT){
            this.currentHeat = buf.readVarInt();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = super.serializeNBT();
        tag.setInteger("heat", this.currentHeat);
        tag.setBoolean("lastIsMOX", this.lastIsMOX);
        tag.setInteger("waterToConsume", this.waterToConsume);
        tag.setFloat("lastRodLevel", this.lastRodLevel);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        this.currentHeat = compound.getInteger("heat");
        this.lastIsMOX = compound.getBoolean("lastIsMOX");
        this.waterToConsume = compound.getInteger("waterToConsume");
        this.lastRodLevel = compound.getFloat("lastRodLevel");
    }

    @Override
    protected long getEnergyInputPerSecond() {
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        return 0;
    }

    @Override
    protected long getEnergyCapacity() {
        return 0;
    }

    @Override
    protected boolean drawEnergy(int i, boolean b) {
        return false;
    }

    @Override
    protected long getMaxVoltage() {
        return 0;
    }
}
