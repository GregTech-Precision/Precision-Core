package gtwp.blocks;

import gregtech.common.blocks.VariantActiveBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

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
        FLUID_STORAGE_T1("fluid_storage_t1"),
        FLUID_STORAGE_T2("fluid_storage_t2"),
        FLUID_STORAGE_T3("fluid_storage_t3"),
        FLUID_STORAGE_T4("fluid_storage_t4");

        private final String name;

        MultiTankFluidStorage(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }
    }

}
