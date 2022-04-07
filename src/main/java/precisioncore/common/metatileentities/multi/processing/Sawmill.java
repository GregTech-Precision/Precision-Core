package precisioncore.common.metatileentities.multi.processing;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import precisioncore.api.capability.impl.SawmillRecipeLogic;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import precisioncore.api.utils.PrecisionChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class Sawmill extends RecipeMapMultiblockController {

    private short mode = 0;

    public Sawmill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PrecisionRecipeMaps.SAWMILL);
        this.recipeMapWorkable = new SawmillRecipeLogic(this);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        mode++;
        if(mode >= 3) mode = 0;
        if(getWorld().isRemote)
            PrecisionChatUtils.sendMessage(playerIn, "Current mode: " + mode);
        return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return null;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.WOOD_WALL;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new Sawmill(metaTileEntityId);
    }
}
