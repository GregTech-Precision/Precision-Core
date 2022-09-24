package precisioncore.common.items;


import gregtech.api.items.metaitem.StandardMetaItem;
import precisioncore.common.items.behaviors.NuclearFuelBehavior;

import static precisioncore.common.items.PrecisionMetaItems.*;

public class PrecisionMetaItem1 extends StandardMetaItem {

    PrecisionMetaItem1(){
        super();
    }

    @Override
    public void registerSubItems() {
        int id = 0;
        //WOODEN_PILE = addItem(id++, "pile.wood").addOreDict("pile_wooden").setUnificationData(dust, Wood);
        NAQUADAH_ROD = addItem(id++, "naquadah_rod").addComponents(new NuclearFuelBehavior(100000, 1, false));
        DUAL_NAQUADAH_ROD = addItem(id++, "dual_naquadah_rod").addComponents(new NuclearFuelBehavior(100000, 2, false));
        QUAD_NAQUADAH_ROD = addItem(id++, "quad_naquadah_rod").addComponents(new NuclearFuelBehavior(100000, 4, false));
        NAQUADAH_MOX_ROD = addItem(id++, "naquadah_mox_rod").addComponents(new NuclearFuelBehavior(100000, 1, true));
        DUAL_NAQUADAH_MOX_ROD = addItem(id++, "dual_naquadah_mox_rod").addComponents(new NuclearFuelBehavior(100000, 2, true));
        QUAD_NAQUADAH_MOX_ROD = addItem(id++, "quad_naquadah_mox_rod").addComponents(new NuclearFuelBehavior(100000, 4, true));
    }
}
