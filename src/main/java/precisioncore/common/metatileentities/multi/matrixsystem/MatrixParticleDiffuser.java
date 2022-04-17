package precisioncore.common.metatileentities.multi.matrixsystem;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import gregtech.GregTechMod;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.particle.GTLaserBeamParticle;
import gregtech.client.particle.GTParticle;
import gregtech.client.particle.GTParticleManager;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import precisioncore.PrecisionCore;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.api.utils.PrecisionUtility;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.BlockIGlass;
import precisioncore.common.blocks.PrecisionMetaBlocks;

import javax.annotation.Nonnull;
import java.util.Random;

public class MatrixParticleDiffuser extends MultiblockWithDisplayBase {

    private int distance = 0;
    private GTParticle particle;

    public MatrixParticleDiffuser(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if(getOffsetTimer() % 8 == 0) {
            int newDistance = getDistanceToStabilizer();
            if (newDistance != distance)
                distance = newDistance;

            generateUnstableParticles();
        }
    }
    
    private void generateUnstableParticles(){
        if (distance > 0) {
            MatrixParticleStabilizer stabilizer = ((MatrixParticleStabilizer) ((MetaTileEntityHolder) getWorld().getTileEntity(getPos().offset(getRightFacing(), 10 + distance))).getMetaTileEntity());
            if (stabilizer != null){
                for(int i = 8; i>0;i--)
                    stabilizer.addUnstableParticles(PrecisionUtility.randomBetween(50,200));
                drawBeam();
            }
        }
    }

    private int getDistanceToStabilizer(){
        for(int i = 5; i<30;i++){
            TileEntity te = getWorld().getTileEntity(getPos().offset(getRightFacing(), 10+i));
            if(te instanceof MetaTileEntityHolder){
                if(((MetaTileEntityHolder) te).getMetaTileEntity() instanceof MatrixParticleStabilizer){
                    if(((MatrixParticleStabilizer) ((MetaTileEntityHolder) te).getMetaTileEntity()).isStructureFormed()) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    private EnumFacing getRightFacing(){
        return getFrontFacing().rotateAround(EnumFacing.Axis.Y).getOpposite();
    }

    private void drawBeam(){
        if(particle != null)
            GTParticleManager.INSTANCE.clearAllEffects(true);

        Vector3 halfOffset = Vector3.fromBlockPos(new BlockPos(0,1,0).offset(getFrontFacing().getOpposite())).$times(0.5);

        BlockPos start = getPos().offset(getFrontFacing().getOpposite()).offset(getRightFacing(), 9);

        Vector3 startPos = Vector3.fromBlockPos(start).$plus(halfOffset);
        Vector3 endPos = Vector3.fromBlockPos(start.offset(getRightFacing(), distance-2)).$plus(halfOffset);
        particle = new GTLaserBeamParticle(getWorld(), startPos, endPos)
                .setBody(new ResourceLocation(GTValues.MODID, "textures/fx/beam.png"));

        GTParticleManager.INSTANCE.addEffect(particle);
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
                .aisle("##########", "#####CCCCC", "#####CCCCC", "#####C###C")
                .aisle("CCCCCCCCCC", "CCGGGCCCCC", "CCCCCCCCCC", "######CCC#")
                .aisle("CCCCCCCCCC", "C*******TG", "CCGGGCCCCC", "######CCC#")
                .aisle("CCCCCCCCCC", "SCGGGCCCCC", "CCCCCCCCCC", "######CCC#")
                .aisle("##########", "#####CCCCC", "#####CCCCC", "#####C###C")
                .where('S', selfPredicate())
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.PARTICLE)).or(autoAbilities(true, false)).or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(4)))
                .where('G', BlockIGlass.predicate())
                .where('T', any()) //change to transducer
                .where('#', any())
                .where('*', air())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.CASING_PARTICLE;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MatrixParticleDiffuser(metaTileEntityId);
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
