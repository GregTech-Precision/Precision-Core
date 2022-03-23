package gtwp.common.blocks;

import gregtech.common.blocks.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class BlockCasing extends VariantBlock<BlockCasing.Casings> {

    public BlockCasing() {
        super(Material.IRON);
        setUnlocalizedName("casing");
        setHardness(5.0f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(Casings.COMPUTER));
    }

    public enum Casings implements IStringSerializable {
        EXTRADIFICATION("extradification"),
        COMPUTER("computer"),
        SATELLITE("satellite");

        private final String name;

        Casings(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
