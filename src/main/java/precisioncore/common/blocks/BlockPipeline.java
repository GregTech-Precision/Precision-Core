package precisioncore.common.blocks;

import gregtech.common.blocks.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class BlockPipeline extends VariantBlock<BlockPipeline.PipelineType> {


    public BlockPipeline() {
        super(Material.IRON);
        setUnlocalizedName("pipelineblock");
        setHardness(5.0f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(PipelineType.FLUID));
    }

    public enum PipelineType implements IStringSerializable{
        FLUID("fluidpipeline", 0),
        ENERGY("energypipeline", 1),
        ITEM("itempipeline", 2);

        private final String name;
        private final int type; //0 - fluid, 1 - energy, 2 - item

        PipelineType(String name, int type) { this.name = name; this.type = type; }

        public String getName() { return this.name; }

        public int getPipeType() { return this.type; }
    }
}
