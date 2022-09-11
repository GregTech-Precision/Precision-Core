package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.IReactorHatch;
import precisioncore.api.capability.impl.ReactorLogic;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;

import java.util.List;

public class Reactor extends MultiblockWithDisplayBase {

    private final ReactorLogic reactorLogic;

    public Reactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.reactorLogic = new ReactorLogic(this, 6);
    }

    @Override
    protected void updateFormedValid() {
    }

    private void initializeAbilities(){
        this.importFluids = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.exportFluids = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetAbilities(){
        this.importFluids = new FluidTankList(true);
        this.exportFluids = new FluidTankList(true);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        notifyOnRodChanges();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetAbilities();
    }

    public void notifyOnRodChanges(){
        reactorLogic.onRodChanges();
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if(capability == GregtechTileCapabilities.CAPABILITY_WORKABLE)
            return null;
        return super.getCapability(capability, side);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("##CCCCC##", "##CCCCC##", "###CCC###", "###CCC###", "####C####", "#########")
                .aisle("#CCCCCCC#", "#C#####C#", "##R###R##", "##R###R##", "##RC#CR##", "##CCCCC##")
                .aisle("CCCCCCCCC", "C#######C", "#R#####R#", "#R#####R#", "#R#####R#", "#CFFFFFC#")
                .aisle("CCCCCCCCC", "C#######C", "C#######C", "C#######C", "#C#####C#", "#CFFFFFC#")
                .aisle("CCCCCCCCC", "C#######C", "C#######C", "C#######C", "C#######C", "#CFFFFFC#")
                .aisle("CCCCCCCCC", "C#######C", "C#######C", "C#######C", "#C#####C#", "#CFFFFFC#")
                .aisle("CCCCCCCCC", "C#######C", "#R#####R#", "#R#####R#", "#R#####R#", "#CFFFFFC#")
                .aisle("#CCCCCCC#", "#C#####C#", "##R###R##", "##R###R##", "##RC#CR##", "##CCCCC##")
                .aisle("##CCCCC##", "##CCSCC##", "###CCC###", "###CCC###", "####C####", "#########")
                .where('S', selfPredicate())
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.REACTOR))
                        .or(autoAbilities(true, false))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(6))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(3)))
                .where('R', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('F', abilities(PrecisionMultiblockAbility.REACTOR_HATCH))
                .where('#', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.REACTOR_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new Reactor(metaTileEntityId);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.QUANTUM_TANK_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if(playerIn.isSneaking())
            downAllRods();
        else
            upAllRods();
        return true;
    }

    private void downAllRods(){
        getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).forEach(IReactorHatch::downRod);
    }

    private void upAllRods(){
        getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).forEach(IReactorHatch::upRod);
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentString(String.format("Heat: %.1f", reactorLogic.getCurrentHeatPercentage() * 100)));
        textList.add(new TextComponentString("Water Consumption: " + reactorLogic.getCurrentWaterConsumption()));
        textList.add(new TextComponentString("Steam Production: " + reactorLogic.getCurrentSteamProduction()));
    }
}
