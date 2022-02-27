package gtwp.common.metatileentities.multi;

import com.sun.jna.platform.win32.WinUser;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gtwp.api.capability.impl.TimedOutputRecipeLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class PyrolyseOven extends RecipeMapMultiblockController {

    public PyrolyseOven(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
        this.recipeMapWorkable = new TimedOutputRecipeLogic(this);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("###", "###", "###")
                .aisle("###", "#S#", "###")
                .aisle("###", "###", "###")
                .where('S', selfPredicate())
                .where('#', states(getCasingState()))
                .build();
    }

    private IBlockState getCasingState(){
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new PyrolyseOven(metaTileEntityId);
    }
}
