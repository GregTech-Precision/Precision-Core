package gtwp.common.items;


import gregtech.api.items.metaitem.StandardMetaItem;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gtwp.common.items.GTWPMetaItems.*;


public class GTWPMetaItem1 extends StandardMetaItem {

    GTWPMetaItem1(){
        super();
    }

    @Override
    public void registerSubItems() {
        int id = 0;

        //WOODEN_PILE = addItem(id++, "pile.wood").addOreDict("pile_wooden").setUnificationData(dust, Wood);
    }
}
