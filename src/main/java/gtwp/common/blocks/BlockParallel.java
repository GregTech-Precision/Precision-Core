package gtwp.common.blocks;

import gregtech.common.blocks.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class BlockParallel extends VariantBlock<BlockParallel.ParallelCasing> {


    public BlockParallel() {
        super(Material.IRON);
        setUnlocalizedName("parallelCasing");
        setHardness(5.0f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(ParallelCasing.PARALLEL_CASING_T1));
    }

    public enum ParallelCasing implements IStringSerializable {
        PARALLEL_CASING_T1("parallel_casing_t1", 4),
        PARALLEL_CASING_T2("parallel_casing_t2", 16),
        PARALLEL_CASING_T3("parallel_casing_t3", 64),
        PARALLEL_CASING_T4("parallel_casing_t4", 256);

        private final String name;
        private final int parallelCount;

        ParallelCasing(String name, int parallelCount){
            this.name = name;
            this.parallelCount = parallelCount;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getParallelCount() {
            return parallelCount;
        }
    }
}
