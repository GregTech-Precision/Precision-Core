package precisioncore.common.items;

import gregtech.api.items.metaitem.MetaItem;

public class PrecisionMetaItems {

    //public static MetaItem<?>.MetaValueItem WOODEN_PILE;
    public static MetaItem<?>.MetaValueItem URANIUM_FUEL;
    public static MetaItem<?>.MetaValueItem URANIUM_MOX_FUEL;
    public static MetaItem<?>.MetaValueItem NAQUADAH_FUEL;
    public static MetaItem<?>.MetaValueItem NAQUADAH_MOX_FUEL;
    public static void init(){
        PrecisionMetaItem1 metaItem1 = new PrecisionMetaItem1();
        metaItem1.setRegistryName("precisioncore.metaitem");
    }
}
