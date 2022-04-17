package precisioncore.common.metatileentities.multi.matrixsystem;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.BlockIGlass;
import precisioncore.common.blocks.PrecisionMetaBlocks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class MatrixParticleContainment extends MultiblockWithDisplayBase {

    private int particlesContained = 0;

    public MatrixParticleContainment(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {}

    public void addStableParticles(int toAdd){
        if(particlesContained < 100000){
            if(particlesContained + toAdd >= 100000)
                particlesContained = 100000;
            else
                particlesContained += toAdd;
        }
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentString("particles contained: "+particlesContained));
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("C####CC", "CCCCCCC", "######C")
                .aisle("CCCCCCC", "CCCCCCG", "CCCCCCC")
                .aisle("CCCCCCC", "G*****G", "CGGGCCC")
                .aisle("CCCCCCC", "CCCCCCG", "CCCCCCC")
                .aisle("C####CC", "CCCCCSC", "######C")
                .where('S', selfPredicate())
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.PARTICLE)).or(autoAbilities(true, false)).or(abilities(MultiblockAbility.INPUT_ENERGY)))
                .where('G', BlockIGlass.predicate())
                .where('#', any())
                .where('*', air())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.CASING_PARTICLE;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MatrixParticleContainment(metaTileEntityId);
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return true;
    }
}
