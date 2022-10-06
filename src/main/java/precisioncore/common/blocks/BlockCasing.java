package precisioncore.common.blocks;

import gregtech.api.block.VariantBlock;
import gregtech.api.pattern.TraceabilityPredicate;
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

    public static TraceabilityPredicate predicate(Casings variant){
        return new TraceabilityPredicate(blockWorldState -> blockWorldState.getBlockState() == PrecisionMetaBlocks.CASING.getState(variant));
    }

    public enum Casings implements IStringSerializable {
        COMPUTER("computer"),
        SATELLITE("satellite"),
        REACTOR("reactor"),
        ADVANCED_TURBINE("advanced_turbine");

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
