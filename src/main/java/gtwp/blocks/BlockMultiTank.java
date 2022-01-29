package gtwp.blocks;

import gregtech.common.blocks.VariantActiveBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMultiTank extends VariantActiveBlock<BlockMultiTank.MultiTankFluidStorage> {

    public BlockMultiTank() {
        super(Material.IRON);
        setUnlocalizedName("multi_tank");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(MultiTankFluidStorage.FLUID_STORAGE_T1));

    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityLiving.SpawnPlacementType type) {
        return false;
    }
    public enum MultiTankFluidStorage implements IStringSerializable {
        FLUID_STORAGE_T1("fluid_storage_t1", 16000000),
        FLUID_STORAGE_T2("fluid_storage_t2", 32000000),
        FLUID_STORAGE_T3("fluid_storage_t3", 64000000),
        FLUID_STORAGE_T4("fluid_storage_t4", 128000000);

        private final String name;
        private final int capacity;

        MultiTankFluidStorage(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }

        @Nonnull
        public int getCapacity() { return this.capacity; }
    }

}
