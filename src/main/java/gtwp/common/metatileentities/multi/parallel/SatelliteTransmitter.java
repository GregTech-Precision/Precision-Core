package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.ITransmitter;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.api.utils.ParallelAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class SatelliteTransmitter extends MetaTileEntityMultiblockPart implements ITransmitter {

    private int frequency = 0;
    private UUID netAddress;

    public SatelliteTransmitter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new SatelliteTransmitter(metaTileEntityId);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (playerIn.isSneaking()) frequency--;
        else frequency++;
        ParallelAPI.removeSatelliteTransmitter(netAddress);
        netAddress = generateNetAddress(playerIn, frequency);
        ParallelAPI.addSatelliteTransmitter(netAddress, this);
        if (getWorld().isRemote){
            GTWPChatUtils.sendMessage(playerIn, "Transmitter frequency: " + frequency);
            GTWPChatUtils.sendMessage(playerIn, "UUID: " + netAddress);
        }

        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public UUID generateNetAddress(EntityPlayer player, int frequency) {
        return UUID.nameUUIDFromBytes((player.getName()+frequency).getBytes());
    }

    @Override
    public UUID getNetAddress() {
        return netAddress;
    }

    @Override
    public boolean isTransmitting() {
        if(getController() != null)
            return getController().isActive();
        return false;
    }

//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound data) {
//        data.setUniqueId("netAddress", netAddress);
//        data.setInteger("frequency", frequency);
//        return super.writeToNBT(data);
//    }
//
//    @Override
//    public void readFromNBT(NBTTagCompound data) {
//        netAddress = data.getUniqueId("netAddress");
//        frequency = data.getInteger("frequency");
//        super.readFromNBT(data);
//    }
}
