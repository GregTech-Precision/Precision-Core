package precisioncore.integration.theoneprobe.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import gregtech.integration.theoneprobe.provider.CapabilityInfoProvider;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.PrecisionCore;
import precisioncore.api.capability.IParallelHatch;
import precisioncore.api.capability.PrecisionCapabilities;
import precisioncore.common.metatileentities.multi.parallel.ParallelHatch;

public class ParallelProvider extends CapabilityInfoProvider<IParallelHatch> {

    @Override
    protected Capability<IParallelHatch> getCapability() {
        return PrecisionCapabilities.CAPABILITY_PARALLEL;
    }

    //Machine:          [No Target Machine/Controller Name]
    //Address:          [No target Address/8 digits from UUID]
    //Connection:       [No Connection/Connection Established]
    //Parallel Points:  [Parallel Points Amount]
    @Override
    protected void addProbeInfo(IParallelHatch capability, IProbeInfo probeInfo, EntityPlayer entityPlayer, TileEntity tileEntity, IProbeHitData iProbeHitData) {
        ParallelHatch hatch = ((ParallelHatch) capability);
        String controllerName = null;

        if(hatch.isTransmitter()){
            if (hatch.isConnected() && hatch.getPair().getController() != null) {
                controllerName = hatch.getPair().getController().getMetaFullName();
            }
        }else{
            if(hatch.getController() != null){
                controllerName = hatch.getController().getMetaFullName();
            }
        }

        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
        vertical.text(TextStyleClass.INFO + (controllerName == null ? "{*precisioncore.top.parallel.target.null*}" : I18n.format(controllerName)));
        vertical.text(TextStyleClass.INFO + "{*precisioncore.top.parallel.address*} " + (hatch.getAddress() == null ? "{*precisioncore.top.parallel.address.null*}" : hatch.getAddress().toString().substring(0,8)));
        vertical.text(TextStyleClass.INFO + (hatch.isConnected() ? ChatFormatting.GREEN + "{*precisioncore.top.connection.established*}" : ChatFormatting.RED + "{*precisioncore.top.connection.no*}"));
        vertical.text(TextStyleClass.INFO + "{*precisioncore.top.parallel.points*} " + capability.getParallel());
    }

    @Override
    public String getID() {
        return PrecisionCore.MODID+":parallel_provider";
    }
}
