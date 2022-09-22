package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.FuelMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.impl.AdvancedTurbineLogic;
import precisioncore.api.recipes.PrecisionRecipeMaps;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;

public class AdvancedTurbine extends FuelMultiblockController {

    public AdvancedTurbine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PrecisionRecipeMaps.ADVANCED_TURBINE, 6);
        this.recipeMapWorkable = new AdvancedTurbineLogic(this);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.recipeMapWorkable.invalidate();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        getRecipeMapWorkable().updateTanks();
    }

    @Override
    public AdvancedTurbineLogic getRecipeMapWorkable() {
        return ((AdvancedTurbineLogic) super.getRecipeMapWorkable());
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
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(6))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(3))
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
        return PrecisionTextures.ADVANCED_TURBINE_CASING;
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
        boolean active = getRecipeMapWorkable().isWorking();
        boolean base = isStructureFormed();
        Textures.LARGE_TURBINE_ROTOR_RENDERER.renderSided(renderState, translation, pipeline, getFrontFacing(), base, base, active, 0x00FFAA);
        BufferBuilder buffer = renderState.getBuffer();
        int axisOffset = 4 * getFrontFacing().getOpposite().getAxisDirection().getOffset();
        if (getFrontFacing().getAxis() == EnumFacing.Axis.X) {
            translation.translate(axisOffset, 2, 0);
            buffer.pos(getPos().getX() + axisOffset, getPos().getY() + 2, getPos().getZ());
        } else{
            translation.translate(0, 2, axisOffset);
            buffer.pos(getPos().getX(), getPos().getY() + 2, getPos().getZ() + axisOffset);
        }
        if(base) {
            CCRenderState ccRenderState = CCRenderState.instance();
            ccRenderState.reset();
            ccRenderState.bind(buffer);
            Textures.LARGE_TURBINE_ROTOR_RENDERER.renderSided(ccRenderState, translation, new IVertexOperation[]{renderState.lightMatrix}, EnumFacing.UP, base, base, active, 0x00FFAA);
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if(capability == GregtechTileCapabilities.CAPABILITY_WORKABLE)
            return null;
        return super.getCapability(capability, side);
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean canVoidRecipeFluidOutputs() {
        return true;
    }

    @Override
    public boolean canVoidRecipeItemOutputs() {
        return true;
    }

    @Override
    public IMultipleTankHandler getInputFluidInventory() {
        return new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
    }
}
