package precisioncore.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.integration.theoneprobe.provider.CapabilityInfoProvider;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.capability.PrecisionCapabilities;

import javax.annotation.Nonnull;

public class RodProvider extends CapabilityInfoProvider<IReactorHatch> {
    @Nonnull
    @Override
    protected Capability<IReactorHatch> getCapability() {
        return PrecisionCapabilities.CAPABILITY_ROD;
    }

    @Override
    protected void addProbeInfo(IReactorHatch rodHatch, IProbeInfo probeInfo, EntityPlayer entityPlayer, TileEntity tileEntity, IProbeHitData probeHitData) {
        IProbeInfo vertical = probeInfo.vertical(new LayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
        if(rodHatch.hasRod()) {
            boolean isMOX = rodHatch.isMOX();
            vertical.text(I18n.format("precisioncore.top.rod." + (isMOX ? "mox" : "uranium")));
            vertical.text(I18n.format("precisioncore.top.rod.level", rodHatch.getRodLevel() * 10));
        } else {
            vertical.text(I18n.format("precisioncore.top.rod.no"));
        }
    }

    @Override
    public String getID() {
        return GTValues.MODID+":rod_provider";
    }
}
