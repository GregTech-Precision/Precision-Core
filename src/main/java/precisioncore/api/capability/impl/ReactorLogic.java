package precisioncore.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.unification.material.Materials;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.common.metatileentities.multi.nuclear.Reactor;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactorLogic extends AbstractRecipeLogic {

    private final Reactor reactor;

    private static final int STEAM_PER_WATER = 40;
    private static final int DATA_HEAT = 989;

    private final int maxHeat;
    private int currentHeat = 0;

    public ReactorLogic(Reactor reactor, int tier){
        super(reactor, null, false);
        this.reactor = reactor;
        this.maxHeat = (int) GTValues.V[tier];
    }

    @Override
    public void update() {
        if(!reactor.isActive() && currentHeat > 0) {
            setHeat(currentHeat - 1);
        }

        if(reactor.isActive() && currentHeat < maxHeat){
            setHeat(currentHeat + 1);
        }

        if(consumeWater(getCurrentWaterConsumption(), false)){
            consumeWater(getCurrentWaterConsumption(), true);
            outputSteam(getCurrentSteamProduction());
        } else {
            // TODO: boom
        }
    }

    public float getRodLevelPercentage(){
        List<IReactorHatch> reactorHatchList = reactor.getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH);
        float maxRodLevel = reactorHatchList.size() * 10;
        float currentLevel = reactorHatchList.stream().mapToInt(IReactorHatch::getRodLevel).sum();
        return currentLevel / maxRodLevel * 100;
    }

    public float getCurrentHeatPercentage(){
        return ((float) currentHeat / (float) maxHeat) * getRodLevelPercentage();
    }

    public int getCurrentWaterConsumption(){
        return (int) (maxHeat * getCurrentHeatPercentage() / 100);
    }

    public int getCurrentSteamProduction(){
        return getCurrentWaterConsumption() * STEAM_PER_WATER;
    }

    private void setHeat(int heat){
        if(this.currentHeat != heat && !getMetaTileEntity().getWorld().isRemote){
            writeCustomData(DATA_HEAT, buf -> buf.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    private boolean consumeWater(int amount, boolean drain){
        List<IFluidTank> inputs = reactor.getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        for(IFluidTank tank : inputs){
            if(tank.getFluid() != null && tank.getFluid().getFluid() == Materials.Water.getFluid()) {
                amount -= tank.drain(amount, drain).amount;
                if(amount == 0) break;
            }
        }
        return amount <= 0;
    }

    private void outputSteam(int amount){
        List<IFluidTank> outputs = reactor.getAbilities(MultiblockAbility.EXPORT_FLUIDS);
        FluidStack water = Materials.Water.getFluid(amount);
        for(IFluidTank tank : outputs){
            water.amount -= tank.fill(water, true);
        }
    }

    @Override
    public void writeInitialData(@Nonnull PacketBuffer buf) {
        super.writeInitialData(buf);
        buf.writeInt(this.currentHeat);
    }

    @Override
    public void receiveInitialData(@Nonnull PacketBuffer buf) {
        super.receiveInitialData(buf);
        this.currentHeat = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == DATA_HEAT){
            this.currentHeat = buf.readVarInt();
        }
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
