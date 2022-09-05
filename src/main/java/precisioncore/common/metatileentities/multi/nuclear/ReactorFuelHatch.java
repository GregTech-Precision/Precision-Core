package precisioncore.common.metatileentities.multi.nuclear;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;

import java.util.List;

public class ReactorFuelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IReactorHatch>, IReactorHatch {

    public ReactorFuelHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ReactorFuelHatch(metaTileEntityId);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public MultiblockAbility<IReactorHatch> getAbility() {
        return PrecisionMultiblockAbility.REACTOR_HATCH;
    }

    @Override
    public void registerAbilities(List<IReactorHatch> list) {
        list.add(this);
    }
}
