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
        URANIUM_FUEL = addItem(id++, "uranium_fuel").addComponents(new NuclearFuelBehavior(20000, false));
        URANIUM_MOX_FUEL = addItem(id++, "uranium_mox_fuel").addComponents(new NuclearFuelBehavior(20000, true));
        NAQUADAH_FUEL = addItem(id++, "naquadah_fuel").addComponents(new NuclearFuelBehavior(100000, false));
        NAQUADAH_MOX_FUEL = addItem(id++, "naquadah_mox_fuel").addComponents(new NuclearFuelBehavior(100000, true));
    }
}
