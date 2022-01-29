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

    public static BlockMultiTank FLUID_STORAGE;

    public GTWPBlocks() {}

    public static void init()
    {
        FLUID_STORAGE = new BlockMultiTank();
        FLUID_STORAGE.setRegistryName("multi_tank");

    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(FLUID_STORAGE);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), block.getMetaFromState(state), new ModelResourceLocation(block.getRegistryName(), MetaBlocks.statePropertiesToString(state.getProperties())));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}
