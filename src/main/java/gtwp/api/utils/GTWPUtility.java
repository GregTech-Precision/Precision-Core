package gtwp.api.utils;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class GTWPUtility {

    public static int clamp(int min, int max, int value){
        return Math.max(min, Math.min(max, value));
    }

    public static int[] BlockPosToInt3(@Nonnull BlockPos pos){
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos Int3ToBlockPos(int[] Int3){
        if(Int3.length == 3) return new BlockPos(Int3[0], Int3[1], Int3[2]);
        return new BlockPos(0,0,0);
    }
}
