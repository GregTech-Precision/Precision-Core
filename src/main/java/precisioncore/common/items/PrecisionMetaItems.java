package precisioncore.common.items;

import gregtech.api.items.metaitem.MetaItem;

public class PrecisionMetaItems {

    //public static MetaItem<?>.MetaValueItem WOODEN_PILE;
    public static MetaItem<?>.MetaValueItem NAQUADAH_ROD;
    public static MetaItem<?>.MetaValueItem DUAL_NAQUADAH_ROD;
    public static MetaItem<?>.MetaValueItem QUAD_NAQUADAH_ROD;
    public static MetaItem<?>.MetaValueItem NAQUADAH_MOX_ROD;
    public static MetaItem<?>.MetaValueItem DUAL_NAQUADAH_MOX_ROD;
    public static MetaItem<?>.MetaValueItem QUAD_NAQUADAH_MOX_ROD;
    public static void init(){
        PrecisionMetaItem1 metaItem1 = new PrecisionMetaItem1();
        metaItem1.setRegistryName("precisioncore.metaitem");
    }
}
