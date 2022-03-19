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
            if(ParallelAPI.getActualTower(netAddress, getPos()) != null) {
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
        ModularUI ui = screwDriverClick ? frequencyUI(entityPlayer) : super.createUI(entityPlayer);
        screwDriverClick = false;
        return ui;
    }

    private EntityPlayer currentPlayer;

    public void incFrequency(Widget.ClickData data){
        frequency++;
    }

    public void decFrequency(Widget.ClickData data){
        frequency--;
    }

    public void setFrequency(Widget.ClickData data){
        if(!getWorld().isRemote) {
            if (currentPlayer != null) {
                netAddress = generateNetAddress(currentPlayer, frequency);
                GTWPChatUtils.sendMessage(currentPlayer, "Frequency: " + frequency);
            }
        }
    }

    public void closeListener(){
        currentPlayer = null;
    }

    @Override
    public UUID getNetAddress(){
        return netAddress;
    }

    private String getStringFrequency(){
        return "Frequency: "+frequency;
    }

    private ModularUI frequencyUI(EntityPlayer player){
        currentPlayer = player;
        return ModularUI.builder(GTWPGuiTextures.FREQUENCY, 176, 166)
                .bindPlayerInventory(player.inventory)
                .bindCloseListener(this::closeListener)
                .widget(new ClickButtonWidget(8, 8, 16, 16, "+", this::incFrequency))
                .widget(new ClickButtonWidget(8, 26, 16, 16, "-", this::decFrequency))
                .widget(new ClickButtonWidget(8,55, 16, 16, "=", this::setFrequency))
                .dynamicLabel(34, 20, this::getStringFrequency, 0xFFFFFF)
                .build(getHolder(), player);
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
        if(data.hasKey("netAddress"))
            netAddress = data.getUniqueId("netAddress");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.frequency);
        buf.writeBoolean(netAddress != null);
        if(netAddress != null)
            buf.writeUniqueId(netAddress);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.frequency = buf.readInt();
        if(buf.readBoolean())
            netAddress = buf.readUniqueId();
        else netAddress = null;
    }
}
