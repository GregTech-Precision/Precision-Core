package gtwp.metatileentities.multi.multiblockparts;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.MetaTileEntityTankValve;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.render.GTWPTextures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MetaTileEntityIOHatch extends MetaTileEntityTankValve {

    public MetaTileEntityIOHatch(ResourceLocation metaTileEntityId)
    {
        super(metaTileEntityId, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new MetaTileEntityIOHatch(metaTileEntityId);
    }
}
