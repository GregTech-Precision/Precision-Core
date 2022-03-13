package gtwp;

import gtwp.common.CommonProxy;
import gtwp.common.blocks.GTWPMetaBlocks;
import gtwp.common.items.GTWPMetaItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import gtwp.common.metatileentities.GTWPMetaTileEntities;

@Mod(modid = GTWP.MODID,
        name = GTWP.NAME,
        version = GTWP.VERSION,
        dependencies = "required-after:gregtech")
public class GTWP {

    public GTWP(){}

    public static final String MODID = "gtwp";
    public static final String NAME = "GT:WP";
    public static final String VERSION = "0.0.4";

    @SidedProxy(modId = MODID, clientSide = "gtwp.common.ClientProxy", serverSide = "gtwp.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {


        GTWPMetaBlocks.init();
        GTWPMetaTileEntities.init();

        proxy.preLoad();
    }
}
