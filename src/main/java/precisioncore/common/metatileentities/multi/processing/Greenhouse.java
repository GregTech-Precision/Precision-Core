package precisioncore.common.metatileentities.multi.processing;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.*;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import precisioncore.common.blocks.BlockIGlass;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class Greenhouse extends RecipeMapMultiblockController {

    public Greenhouse(ResourceLocation metaTileEntityId){
        super(metaTileEntityId, PrecisionRecipeMaps.GREENHOUSE);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#CCC#", "#####")
                .aisle("C***C", "#GGG#").setRepeatable(3)
                .aisle("#CSC#", "#####")
                .where('S', selfPredicate())
                .where('G', BlockIGlass.predicate())
                .where('C', BlockIGlass.predicate().or(autoAbilities(true, false, true, true, true, false, false).setMaxGlobalLimited(1)))
                .where('*', air())
                .where('#', any())
                .build();
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new Greenhouse(metaTileEntityId);
    }
}
