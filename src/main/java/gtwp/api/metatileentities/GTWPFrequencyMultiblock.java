package gtwp.api.metatileentities;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.TextFieldWidget;
import gregtech.api.gui.widgets.TextFieldWidget2;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.terminal.app.guide.widget.TextBoxWidget;
import gtwp.api.capability.GTWPDataCodes;
import gtwp.api.capability.IAddresable;
import gtwp.api.gui.GTWPGuiTextures;
import gtwp.api.utils.GTWPChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GTWPFrequencyMultiblock extends MultiblockWithDisplayBase implements IAddresable {

    private int frequency = 0;
    private UUID netAddress;
    private boolean screwDriverClick;

    public GTWPFrequencyMultiblock(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

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

    private int l_frequency = 0;
    private EntityPlayer currentPlayer;

    public void incFrequency(Widget.ClickData data){
        l_frequency++;
    }

    public void decFrequency(Widget.ClickData data){
        l_frequency--;
    }

    public void setFrequency(Widget.ClickData data){
        if(!getWorld().isRemote) {
            if (currentPlayer != null) {
                frequency = l_frequency;
                netAddress = generateNetAddress(currentPlayer, frequency);
                GTWPChatUtils.sendMessage(currentPlayer, "Frequency: " + frequency);
            }
        }
    }

    public void closeListener(){
        currentPlayer = null;
        l_frequency = 0;
    }

    public void openListener(){
        l_frequency = frequency;
    }

    @Override
    public UUID getNetAddress(){
        return netAddress;
    }

    private String getStringFrequency(){
        return "Frequency: "+l_frequency;
    }

    private ModularUI frequencyUI(EntityPlayer player){
        currentPlayer = player;
        return ModularUI.builder(GTWPGuiTextures.FREQUENCY, 176, 166)
                .bindPlayerInventory(player.inventory)
                .bindOpenListener(this::openListener)
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
