package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;

public class AdvancedTurbine extends MultiblockWithDisplayBase {

    public AdvancedTurbine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#EEE#", "#EEE#", "#EEE#", "#####")
                .aisle("CCCCC", "CCCCC", "CCCCC" ,"#CCC#")
                .aisle("CCCCC", "CPGPC", "CPPPC" ,"CHHHC")
                .aisle("CCCCC", "CPGPC", "CPGPC" ,"CHHHC")
                .aisle("CCCCC", "CPGPC", "CPPPC" ,"CHHHC")
                .aisle("CCCCC", "CPGPC", "CPPPC" ,"#CCC#")
                .aisle("CCCCC", "CCCCC", "CCCCC", "#####")
                .aisle("#HHH#", "#HSH#", "#HHH#", "#####")
                .where('S', selfPredicate())
                .where('H', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.ADVANCED_TURBINE)))
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.ADVANCED_TURBINE))
                        .or(autoAbilities(true, false)))
                .where('E', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.ADVANCED_TURBINE))
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY)))
                .where('G', states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('#', any())
                .where('*', air())
                .build();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return true;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.REACTOR_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new AdvancedTurbine(metaTileEntityId);
    }

    @Override
    public boolean isValidFrontFacing(EnumFacing facing) {
        return facing.getAxis() != EnumFacing.Axis.Y;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.LARGE_TURBINE_ROTOR_RENDERER.renderSided(renderState, translation, pipeline, getFrontFacing(), true, true, true, 0x00FFAA);
        int axisOffset = 4 * getFrontFacing().getOpposite().getAxisDirection().getOffset();
        if(getFrontFacing().getAxis() == EnumFacing.Axis.X)
            translation.translate(axisOffset, 2, 0);
        else
            translation.translate(0, 2, axisOffset);
        Textures.LARGE_TURBINE_ROTOR_RENDERER.renderSided(renderState, translation, pipeline, EnumFacing.UP, true, true, true, 0x00FFAA);
    }
}
