package precisioncore.common.metatileentities.multi.matrixsystem;

import appeng.block.crafting.BlockCraftingStorage;
import appeng.block.crafting.BlockCraftingUnit;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.metatileentities.PrecisionRecipeMapMultiblockController;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.BlockIGlass;
import precisioncore.common.blocks.PrecisionMetaBlocks;
import precisioncore.common.metatileentities.PrecisionMetaTileEntities;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class MESystemProvider extends PrecisionRecipeMapMultiblockController {

    public MESystemProvider(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PrecisionRecipeMaps.ME_SYSTEM_PROVIDER);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC#", "CGGGC#", "CGGGC#", "CGGGC#")
                .aisle("CCCCCC", "CUFUCC", "CUFUCC", "CGGGC#")
                .aisle("CCCCCC", "CUFUCS", "CUFUCC", "CGGGC#")
                .aisle("CCPCC#", "CGMGC#", "CGGGC#", "CGGGC#")
                .where('S', selfPredicate())
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.ME)).or(autoAbilities(true, true, true, true, true, false, false).setMaxGlobalLimited(9)))
                .where('M', metaTileEntities(PrecisionMetaTileEntities.ME_CONNECTOR))
                .where('P', abilities(PrecisionMultiblockAbility.MATRIX_PARTICLE_IMPORT))
                .where('G', BlockIGlass.predicate())
                .where('U', new TraceabilityPredicate(state -> state.getBlockState().getBlock() instanceof BlockCraftingUnit))
                .where('F', new TraceabilityPredicate(state -> state.getBlockState().getBlock() instanceof BlockCraftingStorage))
                .where('#', any())
                .build();
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.CASING_ME;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MESystemProvider(metaTileEntityId);
    }
}
