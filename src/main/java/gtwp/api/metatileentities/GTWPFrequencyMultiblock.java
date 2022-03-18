package gtwp.api.metatileentities;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.TextFieldWidget;
import gregtech.api.gui.widgets.TextFieldWidget2;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.terminal.app.guide.widget.TextBoxWidget;
import gtwp.api.gui.GTWPGuiTextures;
import gtwp.api.utils.GTWPChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GTWPFrequencyMultiblock extends MultiblockWithDisplayBase {

    private int frequency = 0;
    private UUID netAddress;

    public GTWPFrequencyMultiblock(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (getWorld().isRemote){
            frequencyUI(playerIn);
            GTWPChatUtils.sendMessage(playerIn, "open gui");
        }
        return true;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return frequencyUI(entityPlayer);
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
        if(currentPlayer!=null) {
            frequency = l_frequency;
            netAddress = UUID.nameUUIDFromBytes((currentPlayer.getName()+frequency).getBytes());
            GTWPChatUtils.sendMessage(currentPlayer, "Frequency: "+frequency);
        }
    }

    public void closeListener(){
        currentPlayer = null;
        l_frequency = 0;
    }

    public void openListener(){
        l_frequency = frequency;
    }

    public int getFrequency(){
        return frequency;
    }

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
                .widget(new ClickButtonWidget(8, 27, 16, 16, "-", this::decFrequency))
                .widget(new ClickButtonWidget(8,54, 16, 16, "=", this::setFrequency))
                .dynamicLabel(34, 20, this::getStringFrequency, 0xFFFFFF)
                .build(getHolder(), player);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("frequency", frequency);
        data.setUniqueId("netAddress", netAddress);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        frequency = data.getInteger("frequency");
        netAddress = data.getUniqueId("netAddress");
    }
}
