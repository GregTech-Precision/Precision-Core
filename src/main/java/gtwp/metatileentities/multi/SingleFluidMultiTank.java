package gtwp.metatileentities.multi;

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
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gtwp.blocks.BlockMultiTank;
import gtwp.blocks.GTWPBlocks;
import gtwp.metatileentities.GTWPMetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SingleFluidMultiTank extends MultiblockWithDisplayBase {

    private int capacity = 0;
    private boolean updCapacity = false;

    public SingleFluidMultiTank(ResourceLocation metaTileEntityId)
    {
        super(metaTileEntityId);
        initializeAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new SingleFluidMultiTank(metaTileEntityId);
    }

    protected void initializeAbilities() {
        this.importFluids = new FluidTankList(true, makeFluidTanks());
        this.exportFluids = importFluids;
        this.fluidInventory = new FluidHandlerProxy(this.importFluids, this.exportFluids);
    }

    @Nonnull
    private List<FluidTank> makeFluidTanks() {
        List<FluidTank> fluidTankList = new ArrayList<>(1);
        fluidTankList.add(new FilteredFluidHandler(capacity));
        return fluidTankList;
    }


    private int countCapacity() {
        BlockPos storagePos = getPos();
        int l_capacity = 0;
        for (int i = 0; i < 5; i++)
        {
            storagePos = storagePos.down();
            if(!getWorld().isAirBlock(storagePos))
            {
                IBlockState storageState = getWorld().getBlockState(storagePos);
                Block storage = storageState.getBlock();
                if(storage.getClass() == BlockMultiTank.class)
                {
                    l_capacity += ((BlockMultiTank) storage).getState(storageState).getCapacity();
                }
                else return 0;
            }
            else return 0;
        }
        return l_capacity;
    }

    @Override
    protected void updateFormedValid() {
        capacity = countCapacity();
        initializeAbilities();
        updCapacity = false;
        GTLog.logger.info("capacity updated: " + capacity);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                 .aisle("XXX", "GGG", "GGG", "GGG", "GGG", "GGG", "XXX")
                 .aisle("XXX", "GMG", "GMG", "GMG", "GMG", "GMG", "XSX")
                 .aisle("XXX", "GGG", "GGG", "GGG", "GGG", "GGG", "XXX")
                 .where('S', selfPredicate())
                 .where('X', states(getCasingState()))
                 .where('G', states(getCasingState()).or(metaTileEntities(GTWPMetaTileEntities.IO_HATCH).setMaxGlobalLimited(1)))
                 .where('M', any())
                 .build();
    }

    private IBlockState getCasingState()
    {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    //private IBlockState getGlassState()
   // {
    //    return GTWPBlocks.I_GLASS.getState(BlockIGlass.IGlass.WHITE);
   // }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!isStructureFormed()) return false;
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    @Override
    protected ModularUI.Builder createUITemplate(@Nonnull EntityPlayer entityPlayer) {
        return ModularUI.defaultBuilder()
                .widget(new LabelWidget(6, 6, getMetaFullName()))
                .widget(new TankWidget(importFluids.getTankAt(0), 52, 18, 72, 61)
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setContainerClicking(true, true))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 0);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
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
