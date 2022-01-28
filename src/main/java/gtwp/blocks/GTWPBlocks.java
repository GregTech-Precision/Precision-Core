package gtwp.blocks;

import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GTWPBlocks {

    public static BlockMultiTankFluidStorage MULTI_TANK_STORAGE;

    public GTWPBlocks() {}

    public static void init()
    {
        MULTI_TANK_STORAGE = new BlockMultiTankFluidStorage();
        MULTI_TANK_STORAGE.setRegistryName("multi_tank_fluid_storage");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(MULTI_TANK_STORAGE);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), block.getMetaFromState(state), new ModelResourceLocation(block.getRegistryName(), MetaBlocks.statePropertiesToString(state.getProperties())));
        }
    }
}
