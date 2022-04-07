package precisioncore.api.metatileentities;

import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import precisioncore.api.capability.PrecisionDataCodes;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.capability.IParallelHatch;
import precisioncore.api.capability.IParallelMultiblock;
import precisioncore.api.capability.impl.ParallelRecipeLogic;
import precisioncore.api.utils.PrecisionParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public abstract class PrecisionRecipeMapMultiblockController extends MultiMapMultiblockController implements IParallelMultiblock, IAddresable {

    private int frequency = 0;
    private UUID netAddress;

    public PrecisionRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
    }

    public PrecisionRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new ParallelRecipeLogic(this);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int getMaxParallel() {
        if(!getWorld().isRemote) {
            if(PrecisionParallelAPI.getActualTower(netAddress, getPos()) != null){
                List<IParallelHatch> parallel = getAbilities(PrecisionMultiblockAbility.PARALLEL_HATCH_IN);
                return parallel.isEmpty() ? 1 : parallel.get(0).getParallel();
            }
        }
        return 1;
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn, checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        if (isParallel())
            predicate = predicate.or(abilities(PrecisionMultiblockAbility.PARALLEL_HATCH_IN).setMaxGlobalLimited(1,1));
        return predicate;
    }

    @Override
    public UUID getNetAddress(){
        return netAddress;
    }

    @Override
    public void setNetAddress(UUID netAddress) {
        if(!getWorld().isRemote) {
            this.netAddress = netAddress;
            writeCustomData(PrecisionDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
        }
    }

    @Override
    public void setNetAddress(EntityPlayer player, int frequency) {
        if(!getWorld().isRemote){
            this.frequency = frequency;
            IAddresable.super.setNetAddress(player, frequency);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("frequency", frequency);
        if(netAddress != null)
            data.setUniqueId("netAddress", netAddress);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        frequency = data.getInteger("frequency");
        if(data.hasUniqueId("netAddress"))
            netAddress = data.getUniqueId("netAddress");
        else netAddress = null;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(frequency);
        if(netAddress != null)
            writeCustomData(PrecisionDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        frequency = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == PrecisionDataCodes.NET_ADDRESS_UPDATE)
            netAddress = buf.readUniqueId();
    }

    @Override
    public int getFrequency() {
        return frequency;
    }
}
