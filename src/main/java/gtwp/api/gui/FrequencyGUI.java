package gtwp.api.gui;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.IncrementButtonWidget;
import gregtech.api.gui.widgets.SimpleTextWidget;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.common.terminal.app.guide.widget.TextBoxWidget;
import gtwp.api.capability.IAddresable;
import gtwp.api.utils.GTWPChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

public class FrequencyGUI {

    private EntityPlayer player;
    private MetaTileEntityHolder holder;
    private int frequency;

    public FrequencyGUI(MetaTileEntityHolder holder, EntityPlayer player, int frequency){
        this.holder = holder;
        this.player = player;
        this.frequency = frequency;
    }

    private void changeFrequency(int change){
        frequency+=change;
    }

    private void setFrequency(Widget.ClickData data){
         IAddresable mech = ((IAddresable) holder.getMetaTileEntity());
         mech.setNetAddress(player, frequency);
    }

    private String getFrequencyString(){
        return "Frequency: "+frequency;
    }

    public ModularUI createFrequencyUI(){
        return ModularUI.builder(GTWPGuiTextures.FREQUENCY, 176, 166)
                .bindPlayerInventory(player.inventory)
                .widget(new IncrementButtonWidget(8, 8, 16, 16, 1, 4, 16, 64, this::changeFrequency))
                .widget(new IncrementButtonWidget(8, 26, 16, 16, -1, -4, -16, -64, this::changeFrequency))
                .widget(new ClickButtonWidget(8,55, 16, 16, "=", this::setFrequency))
                .widget(new SimpleTextWidget(50, 8, "", this::getFrequencyString))
                .build(holder, player);
    }
}
