package precisioncore.common.metatileentities.multi.nuclear;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.util.ResourceLocation;
import precisioncore.api.capability.impl.ReactorRecipeLogic;
import precisioncore.api.render.PrecisionTextures;
import precisioncore.common.blocks.BlockCasing;
import precisioncore.common.blocks.PrecisionMetaBlocks;

public class Reactor extends MultiblockWithDisplayBase {

    private final ReactorRecipeLogic reactorRecipeLogic;

    public Reactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.reactorRecipeLogic = new ReactorRecipeLogic(this);
    }

    @Override
    protected void updateFormedValid() {}

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
                .aisle("##CCSCC##", "##CCCCC##", "###CCC###", "###CCC###", "####C####", "#########")
                .where('S', selfPredicate())
                .where('C', states(PrecisionMetaBlocks.CASING.getState(BlockCasing.Casings.REACTOR)))
                .where('R', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('F', any())
                .where('#', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PrecisionTextures.SATELLITE_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new Reactor(metaTileEntityId);
    }
}
