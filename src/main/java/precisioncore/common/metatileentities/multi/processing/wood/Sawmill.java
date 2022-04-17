package precisioncore.common.metatileentities.multi.processing.wood;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

//instead of using sawmill mode i'll use integrated circuits
public class Sawmill extends RecipeMapMultiblockController {

    public Sawmill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PrecisionRecipeMaps.SAWMILL);
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
