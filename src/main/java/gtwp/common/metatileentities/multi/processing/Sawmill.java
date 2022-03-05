package gtwp.common.metatileentities.multi.processing;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gtwp.api.capability.impl.SawmillRecipeLogic;
import gtwp.api.recipes.GTWPRecipeMaps;
import gtwp.api.utils.GTWPChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class Sawmill extends RecipeMapMultiblockController {

    private short mode = 0;

    public Sawmill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTWPRecipeMaps.SAWMILL);
        this.recipeMapWorkable = new SawmillRecipeLogic(this);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        mode++;
        if(mode >= 3) mode = 0;
        if(getWorld().isRemote)
            GTWPChatUtils.sendMessage(playerIn, "Current mode: " + mode);
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
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new Sawmill(metaTileEntityId);
    }
}
