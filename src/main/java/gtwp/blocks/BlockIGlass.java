package gtwp.blocks;

import com.sun.javafx.sg.prism.NodeEffectInput;
import gregtech.common.blocks.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIGlass extends VariantBlock<BlockIGlass.IGlass> {

    public BlockIGlass()
    {
        super(Material.GLASS);
        setUnlocalizedName("iglass");
        setHardness(5.0f);
        setResistance(5.0f);
        setSoundType(SoundType.GLASS);
        setDefaultState(getState(IGlass.WHITE));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    enum IGlass implements IStringSerializable {
        WHITE("iglass0"),
        ORANGE("iglass1"),
        MAGENTA("iglass2"),
        LIGHT_BLUE("iglass3"),
        YELLOW("iglass4"),
        LIME("iglass5"),
        PINK("iglass6"),
        GRAY("iglass7"),
        LIGHT_GRAY("iglass8"),
        CYAN("iglass9"),
        PURPLE("iglass10"),
        BLUE("iglass11"),
        BROWN("iglass12"),
        GREEN("iglass13"),
        RED("iglass14"),
        BLACK("iglass15");

        final String name;

        IGlass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
