package precisioncore.common.metatileentities.multi.nuclear;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.ToggleButtonWidget;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import precisioncore.api.capability.impl.ReactorLogic;
import precisioncore.api.gui.PrecisionGUITextures;
import precisioncore.api.metatileentities.PrecisionMultiblockAbility;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;

import javax.annotation.Nullable;
import java.util.List;

public abstract class NuclearReactor extends MultiblockWithDisplayBase {

    private final ReactorLogic reactorLogic;

    public NuclearReactor(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId);
        this.reactorLogic = new ReactorLogic(this, tier);
    }

    @Override
    protected void updateFormedValid(){}

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
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.REACTOR_CASING;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.QUANTUM_TANK_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(PrecisionGUITextures.NUCLEAR_REACTOR_BACKGROUND, 176, 164)
                .label(11, 9, this.getMetaFullName(), 16777215)
                .widget(new AdvancedTextWidget(11, 19, this::addDisplayText, 16777215))
                .widget(new ToggleButtonWidget(155, 3, 16, 16, PrecisionGUITextures.TURN_BUTTON, reactorLogic::isWorkingEnabled, reactorLogic::setWorkingEnabled))
                .widget(new ClickButtonWidget(155, 35, 16, 16, "", this::clickUpAllRods)
                        .setButtonTexture(PrecisionGUITextures.REACTOR_ROD_UP_BUTTON))
                .widget(new ClickButtonWidget(155, 53, 16, 16, "", this::clickDownAllRods)
                        .setButtonTexture(PrecisionGUITextures.REACTOR_ROD_DOWN_BUTTON))
                .widget(new ProgressWidget(reactorLogic::getCurrentHeatPercentage, 158, 111, 10, 36)
                        .setProgressBar(null, PrecisionGUITextures.REACTOR_HEAT_BAR, ProgressWidget.MoveType.VERTICAL))
                .build(getHolder(), entityPlayer);
    }

    private void clickDownAllRods(Widget.ClickData clickData) {
        int amount = clickData.isShiftClick ? 10 : 1;
        getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).forEach(hatch -> hatch.downRod(amount, false));
        notifyOnRodChanges();
    }

    private void clickUpAllRods(Widget.ClickData clickData) {
        int amount = clickData.isShiftClick ? 10 : 1;
        getAbilities(PrecisionMultiblockAbility.REACTOR_HATCH).forEach(hatch -> hatch.upRod(amount, false));
        notifyOnRodChanges();
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("precisioncore.machine.reactor.info.heat").appendText(String.format(" %.1f", reactorLogic.getCurrentHeatPercentage() * 100)));
        textList.add(new TextComponentTranslation("precisioncore.machine.reactor.info.rod_level").appendText(String.format(" %.1f", reactorLogic.getCurrentRodLevel() * 100)));
        textList.add(new TextComponentTranslation("precisioncore.machine.reactor.info.water_consumption", reactorLogic.getCurrentWaterConsumption()));
        textList.add(new TextComponentTranslation("precisioncore.machine.reactor.info.steam_production", reactorLogic.getCurrentSteamProduction()));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("precisioncore.machine.reactor.tooltip"));
    }

    public static class NuclearReactorT3 extends NuclearReactor {

        public NuclearReactorT3(ResourceLocation metaTileEntityId) {
            super(metaTileEntityId, 6);
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
        public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new NuclearReactorT3(metaTileEntityId);
        }
    }

    public static class NuclearReactorT2 extends NuclearReactor {

        public NuclearReactorT2(ResourceLocation metaTileEntityId) {
            super(metaTileEntityId, 5);
        }

        @Override
        protected BlockPattern createStructurePattern() {
            return FactoryBlockPattern.start()
                    .aisle("#CCCCC#", "#CCCCC#", "##CCC##", "##CCC##", "###C###", "#######")
                    .aisle("CCCCCCC", "CR###RC", "#R###R#", "#R###R#", "#RCCCR#", "##CCC##")
                    .aisle("CCCCCCC", "C#####C", "C#####C", "C#####C", "#C###C#", "#CFFFC#")
                    .aisle("CCCCCCC", "C#####C", "C#####C", "C#####C", "CC###CC", "#CFFFC#")
                    .aisle("CCCCCCC", "C#####C", "C#####C", "C#####C", "#C###C#", "#CFFFC#")
                    .aisle("CCCCCCC", "CR###RC", "#R###R#", "#R###R#", "#RCCCR#", "##CCC##")
                    .aisle("#CCCCC#", "#CCSCC#", "##CCC##", "##CCC##", "###C###", "#######")
                    .where('S', selfPredicate())
                    .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.REACTOR))
                            .or(autoAbilities(true, false))
                            .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(4))
                            .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2)))
                    .where('R', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                    .where('F', abilities(PrecisionMultiblockAbility.REACTOR_HATCH))
                    .where('#', any())
                    .build();
        }
        @Override
        public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new NuclearReactorT2(metaTileEntityId);
        }
    }

    public static class NuclearReactorT1 extends NuclearReactor {

        public NuclearReactorT1(ResourceLocation metaTileEntityId) {
            super(metaTileEntityId, 3);
        }

        @Override
        protected BlockPattern createStructurePattern() {
            return FactoryBlockPattern.start()
                    .aisle("#CCC#", "#CCC#", "##C##", "#####")
                    .aisle("CCCCC", "CR#RC", "#RCR#", "#CCC#")
                    .aisle("CCCCC", "C###C", "CC#CC", "#CFC#")
                    .aisle("CCCCC", "CR#RC", "#RCR#", "#CCC#")
                    .aisle("#CCC#", "#CSC#", "##C##", "#####")
                    .where('S', selfPredicate())
                    .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.REACTOR))
                            .or(autoAbilities(true, false))
                            .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                            .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1)))
                    .where('R', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                    .where('F', abilities(PrecisionMultiblockAbility.REACTOR_HATCH))
                    .where('#', any())
                    .build();
        }
        @Override
        public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new NuclearReactorT1(metaTileEntityId);
        }
    }
}
