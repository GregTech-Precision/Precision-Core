package gtwp.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockMultiTankStorage extends Block {

    private String name;
    private static BlockMultiTankStorage instance;

    public BlockMultiTankStorage(String name)
    {
        super(Material.IRON);
        this.name = name;
    }

    public BlockMultiTankStorage getInstance()
    {
        return instance;
    }

    public void registerBlock()
    {
        super.setUnlocalizedName()
    }
}
