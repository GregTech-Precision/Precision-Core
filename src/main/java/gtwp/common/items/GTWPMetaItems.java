package gtwp.common.items;

import gregtech.api.items.metaitem.MetaItem;

public class GTWPMetaItems {

    //public static MetaItem<?>.MetaValueItem WOODEN_PILE;

    public static void init(){
        GTWPMetaItem1 metaItem1 = new GTWPMetaItem1();
        metaItem1.setRegistryName("gtwp.metaitem");
    }
}
