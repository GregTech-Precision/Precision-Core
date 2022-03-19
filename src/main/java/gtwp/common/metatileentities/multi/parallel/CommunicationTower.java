package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.Widget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import gtwp.api.capability.IAddresable;
import gtwp.api.capability.IReceiver;
import gtwp.api.metatileentities.GTWPFrequencyMultiblock;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.metatileentities.GTWPRecipeMapMultiblockController;
import gtwp.api.utils.ParallelAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class CommunicationTower extends GTWPFrequencyMultiblock {

    public CommunicationTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentString("Satellite connection: " + (isReceivingSignal() ? "online" : "offline")));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("###", "###", "###", "###", "###", "###", "###", "###", "###", "#R#")
                .aisle("#F#", "#S#", "#F#", "#F#", "#F#", "#F#", "#F#", "#F#", "#F#", "RFR")
                .aisle("###", "###", "###", "###", "###", "###", "###", "###", "###", "#R#")
                .where('S', selfPredicate())
                .where('R', abilities(GTWPMultiblockAbility.RECEIVER))
                .where('F', states(MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel)))
                .where('#', any())
                .build();
    }

    public boolean isReceivingSignal(){
        if(!getWorld().isRemote){
            if(isActive()) {
                List<IReceiver> receiverList = getAbilities(GTWPMultiblockAbility.RECEIVER);
                if (!receiverList.isEmpty()) {
                    for (IReceiver receiver : receiverList) {
                        if (receiver instanceof SatelliteReceiver) {
                            if (!(receiver.isConnected() && ((SatelliteReceiver) receiver).getConnection().isTransmitting())) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setFrequency(Widget.ClickData data) {
        if(!getWorld().isRemote) {
            if (getNetAddress() != null)
                ParallelAPI.removeCommunicationTower(getNetAddress(), this);
            super.setFrequency(data);
            ParallelAPI.addCommunicationTower(getNetAddress(), this);
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new CommunicationTower(metaTileEntityId);
    }

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
    public void onUnload() {
        super.onUnload();
        ParallelAPI.removeCommunicationTower(getNetAddress(), this);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        ParallelAPI.removeCommunicationTower(getNetAddress(), this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ParallelAPI.addCommunicationTower(getNetAddress(), this);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        ParallelAPI.addCommunicationTower(getNetAddress(), this);
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }
}
