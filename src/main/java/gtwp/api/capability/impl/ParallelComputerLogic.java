//package gtwp.api.capability.impl;
//
//import gregtech.api.GTValues;
//import gregtech.api.capability.impl.AbstractRecipeLogic;
//import gregtech.api.metatileentity.MetaTileEntity;
//import gregtech.api.metatileentity.multiblock.MultiblockAbility;
//import gregtech.api.recipes.RecipeMap;
//import gregtech.common.metatileentities.MetaTileEntities;
//import gtwp.common.metatileentities.multi.parallel.ParallelComputer;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.items.IItemHandlerModifiable;
//
//import javax.annotation.Nonnull;
//import java.util.List;
//
//public class ParallelComputerLogic {
//
//    private ParallelComputer parallelComputer;
//
//    public ParallelComputerLogic(@Nonnull ParallelComputer computer) {
//        this.parallelComputer = computer;
//
//    }
//
//    private void drainEnergy(){
//        if(parallelComputer.isActive())
//            parallelComputer.drainEnergy(GTValues.V[5],false);
//    }
//}
