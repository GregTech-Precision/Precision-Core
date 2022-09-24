package precisioncore.common.items.behaviors;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NuclearFuelBehavior extends AbstractMaterialPartBehavior {

    private final int maxDurability;
    private final int rodAmount;
    private final boolean MOX;

    public NuclearFuelBehavior(int maxDurability, int rodAmount, boolean MOX){
        this.maxDurability = maxDurability;
        this.rodAmount = rodAmount;
        this.MOX = MOX;
    }

    public int getDurability(ItemStack stack){
        return getPartMaxDurability(stack) - getPartDamage(stack);
    }

    public boolean isMOX() {
        return MOX;
    }

    public void applyRodDamage(ItemStack itemStack, int damageApplied) {
        int rodDurability = getPartMaxDurability(itemStack);
        int resultDamage = getPartDamage(itemStack) + damageApplied;
        if (resultDamage >= rodDurability) {
            itemStack.shrink(1);
        } else {
            setPartDamage(itemStack, resultDamage);
        }
    }

    public int getRodAmount(){
        return rodAmount;
    }

    public int getFuelModifier(){
        return rodAmount * (MOX ? 4 : 1);
    }

    @Override
    public int getPartMaxDurability(ItemStack itemStack) {
        NuclearFuelBehavior behavior = getInstanceFor(itemStack);
        if(behavior == null)
            return 0;
        return behavior.maxDurability;
    }

    @Nullable
    public static NuclearFuelBehavior getInstanceFor(@Nonnull ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof MetaItem))
            return null;

        MetaItem<?>.MetaValueItem valueItem = ((MetaItem<?>) itemStack.getItem()).getItem(itemStack);
        if (valueItem == null)
            return null;

        IItemDurabilityManager durabilityManager = valueItem.getDurabilityManager();
        if (!(durabilityManager instanceof NuclearFuelBehavior))
            return null;

        return (NuclearFuelBehavior) durabilityManager;
    }

    @Override
    public void addInformation(ItemStack itemStack, List<String> lines) {
        lines.add("Durability: " + getDurability(itemStack));
    }
}
