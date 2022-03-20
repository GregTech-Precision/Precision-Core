package gtwp.api.metatileentities;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTLog;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IAddresable;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.capability.IParallelMultiblock;
import gtwp.api.capability.impl.ParallelRecipeLogic;
import gtwp.api.gui.FrequencyGUI;
import gtwp.api.gui.GTWPGuiTextures;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.GTWPUtility;
import gtwp.api.utils.ParallelAPI;
import gtwp.common.metatileentities.multi.parallel.CommunicationTower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public abstract class GTWPRecipeMapMultiblockController extends MultiMapMultiblockController implements IParallelMultiblock, IAddresable {

    private CommunicationTower communicationTower;
    private int frequency = 0;
    private UUID netAddress;

    public GTWPRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
    }

    public GTWPRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
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
            if(communicationTower == null)
                communicationTower = ParallelAPI.getActualTower(netAddress, getPos());

            if(communicationTower != null){
                List<IParallelHatch> parallel = getAbilities(GTWPMultiblockAbility.PARALLEL_HATCH_IN);
                return parallel.isEmpty() ? 1 : parallel.get(0).getParallel();
            }
        }
        return 1;
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn, checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        if (isParallel())
            predicate = predicate.or(abilities(GTWPMultiblockAbility.PARALLEL_HATCH_IN).setMaxGlobalLimited(1,1));
        return predicate;
    }

    //shit to merge later

    private boolean screwDriverClick;

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        screwDriverClick = true;
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI ui = screwDriverClick ? new FrequencyGUI(getHolder(), entityPlayer, frequency).createFrequencyUI() : super.createUI(entityPlayer);
        screwDriverClick = false;
        return ui;
    }

    @Override
    public UUID getNetAddress(){
        return netAddress;
    }

    @Override
    public void setNetAddress(UUID netAddress) {
        this.netAddress = netAddress;
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
            writeCustomData(GTWPDataCodes.NET_ADDRESS_UPDATE, b -> b.writeUniqueId(netAddress));
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        frequency = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == GTWPDataCodes.NET_ADDRESS_UPDATE)
            netAddress = buf.readUniqueId();
    }
}
