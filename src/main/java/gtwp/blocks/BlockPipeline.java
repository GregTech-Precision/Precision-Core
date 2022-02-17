package gtwp.blocks;

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

    enum PipelineType implements IStringSerializable{
        FLUID("fluidpipeline", );

        final String name;
        final byte type; //0 - fluid, 1 - energy, 2 - item

        public String getName() { return name; }

        public byte getType() { return type; }

        PipelineType(String name, byte type) { this.name = name; this.type = type; }
    }
}
