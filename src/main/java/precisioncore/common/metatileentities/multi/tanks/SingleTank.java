package precisioncore.common.metatileentities.multi.tanks;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import precisioncore.common.blocks.BlockIGlass;
import precisioncore.common.blocks.BlockMultiTank;
import precisioncore.common.metatileentities.PrecisionMetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SingleTank extends MultiblockWithDisplayBase {

    private boolean recalc = true;
    private FilteredFluidHandler fluidHandler;

    public SingleTank(ResourceLocation metaTileEntityId)
    {
        super(metaTileEntityId);
        initializeAbilities();
    }


    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SingleTank(metaTileEntityId);
    }

    protected void initializeAbilities() {
        this.importFluids = new FluidTankList(true, makeFluidTanks());
        this.exportFluids = importFluids;
        this.fluidInventory = new FluidHandlerProxy(this.importFluids, this.exportFluids);
    }

    @Nonnull
    private List<FluidTank> makeFluidTanks() {
        List<FluidTank> fluidTankList = new ArrayList<>(1);
        fluidHandler = new FilteredFluidHandler(0);
        fluidTankList.add(fluidHandler);
        return fluidTankList;
    }

    @Override
    public boolean canShare() {
        return false;
    }

    private int countCapacity() {
        BlockPos storagePos = getPos();
        int outCapacity = 0;
        for (int i = 0; i < 5; i++)
        {
            storagePos = storagePos.down();
            if(!getWorld().isAirBlock(storagePos))
            {
                IBlockState storageState = getWorld().getBlockState(storagePos);
                Block storage = storageState.getBlock();
                if(storage instanceof BlockMultiTank)
                    outCapacity += ((BlockMultiTank) storage).getState(storageState).getCapacity();
                else return 0;
            }
            else return 0;
        }
        return outCapacity;
    }

    @Override
    public void update() {
        super.update();
        if(!recalc && !isStructureFormed()) {
            recalc = true;
            fluidHandler.setCapacity(0);
        }
    }

    @Override
    protected void updateFormedValid() {
        if(recalc) {
            recalc = false;
            fluidHandler.setCapacity(countCapacity());
        }
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                 .aisle("XXX", "GGG", "GGG", "GGG", "GGG", "GGG", "XXX")
                 .aisle("XXX", "GMG", "GMG", "GMG", "GMG", "GMG", "XSX")
                 .aisle("XXX", "GGG", "GGG", "GGG", "GGG", "GGG", "XXX")
                 .where('S', selfPredicate())
                 .where('X', states(getCasingState()))
                 .where('G', new TraceabilityPredicate(glassPredicate()).or(metaTileEntities(PrecisionMetaTileEntities.IO_HATCH).setMaxGlobalLimited(1)))
                 .where('M', new TraceabilityPredicate(storagePredicate()))
                 .build();
    }

    private IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PTFE_INERT_CASING);
    }

    public static Predicate<BlockWorldState> glassPredicate() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockIGlass;
        };
    }

    public static Predicate<BlockWorldState> storagePredicate() {
        return (blockWorldState) -> {
            return blockWorldState.getBlockState().getBlock() instanceof BlockMultiTank;
        };
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!isStructureFormed() || fluidHandler.getCapacity() == 0) return false;
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    /*@Override
    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing wrenchSide, CuboidRayTraceResult hitResult) {
        setFrontFacing(wrenchSide);
        return super.onWrenchClick(playerIn, hand, wrenchSide, hitResult);
    }

    @Override
    public boolean hasFrontFacing() {
        return true;
    }*/

    @Override
    protected ModularUI.Builder createUITemplate(@Nonnull EntityPlayer entityPlayer) {
        return ModularUI.defaultBuilder()
                .widget(new LabelWidget(6, 6, getMetaFullName()))
                .widget(new TankWidget(exportFluids.getTankAt(0), 52, 18, 72, 61)
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setContainerClicking(true, true))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 0);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.INERT_PTFE_CASING;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(EnumFacing.UP, renderState, translation, pipeline);
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.QUANTUM_TANK_OVERLAY;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.tank.tooltip"));
        tooltip.add(I18n.format("gregtech.machine.quantum_tank.capacity"));
    }
}
