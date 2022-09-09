package precisioncore.common.items.behaviors;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NuclearFuelBehavior implements IItemBehaviour {

    private int durability;
    private boolean MOX;

    public NuclearFuelBehavior(int durability, boolean MOX){
        this.durability = durability;
        this.MOX = MOX;
    }

    public boolean isMOX() {
        return MOX;
    }

    public int getDurability() {
        return durability;
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
        IItemBehaviour.super.addInformation(itemStack, lines);
        lines.add("Durability: " + durability);
    }
}
