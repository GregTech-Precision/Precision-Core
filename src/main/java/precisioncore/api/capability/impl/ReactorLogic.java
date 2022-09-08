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

    private static final int STEAM_PER_WATER = 160;
    private static final int DATA_HEAT = 989;

    private final int maxHeat;
    private int currentHeat = 0;


    /**
     * Produces steam equals energy (Reactor tier + 1) * 10A
     */
    public ReactorLogic(Reactor reactor, int tier){
        super(reactor, null, false);
        this.maxHeat = (int) GTValues.V[tier-1];
        this.workingEnabled = false;
    }

    @Override
    public Reactor getMetaTileEntity() {
        return (Reactor) super.getMetaTileEntity();
    }

    @Override
    public void update() {
        if((!getMetaTileEntity().isActive() || !isWorkingEnabled()) && currentHeat > 0) {
            setHeat(currentHeat - 1);
        }

        if(getMetaTileEntity().isActive() && isWorkingEnabled() && currentHeat < maxHeat){
            setHeat(currentHeat + 1);
        }

        int waterToConsume = (int)(getCurrentWaterConsumption());
        if(consumeWater(waterToConsume, false)){
            consumeWater(waterToConsume, true);
            outputSteam(getCurrentSteamProduction());
        } else if(waterToConsume != 0) {
            getMetaTileEntity().doExplosion(100*getCurrentHeatPercentage());
        }
    }

    public float getRodLevelPercentage(){
        List<IReactorHatch> reactorHatchList = getMetaTileEntity().getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH);
        float maxRodLevel = reactorHatchList.size() * 10;
        float currentLevel = reactorHatchList.stream().mapToInt(IReactorHatch::getRodLevel).sum();
        return currentLevel / maxRodLevel;
    }

    public float getCurrentHeatPercentage(){
        return Math.min(getRodLevelPercentage(), ((float) currentHeat / (float) maxHeat));
    }

    public float getCurrentWaterConsumption(){
        return maxHeat * getCurrentHeatPercentage();
    }

    public int getCurrentSteamProduction(){
        return (int) getCurrentWaterConsumption() * STEAM_PER_WATER;
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
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        this.currentHeat = compound.getInteger("heat");
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
