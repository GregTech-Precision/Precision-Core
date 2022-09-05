package precisioncore.integration.theoneprobe.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import gregtech.integration.theoneprobe.provider.CapabilityInfoProvider;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.PrecisionCore;
import precisioncore.api.capability.IAddresable;
import precisioncore.api.capability.PrecisionCapabilities;

public class AddressableProvider extends CapabilityInfoProvider<IAddresable> {

    @Override
    protected Capability<IAddresable> getCapability() {
        return PrecisionCapabilities.CAPABILITY_ADDRESSABLE;
    }

    @Override
    protected void addProbeInfo(IAddresable capability, IProbeInfo probeInfo, EntityPlayer entityPlayer, TileEntity tileEntity, IProbeHitData iProbeHitData) {
        IProbeInfo horizontal = probeInfo.vertical(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
        horizontal.text(TextStyleClass.INFO + "{*precisioncore.top.addressable.address*} " + (capability.getNetAddress() == null ? "{*precisioncore.top.addressable.address.null*}" : capability.getNetAddress().toString().substring(0,8)));
        horizontal.text(TextStyleClass.INFO + (capability.isReceivingSignal() ? ChatFormatting.GREEN + "{*precisioncore.top.connection.established*}" : ChatFormatting.RED + "{*precisioncore.top.connection.no*}"));
    }

    @Override
    public String getID() {
        return PrecisionCore.MODID+"addressable_provider";
    }
}
