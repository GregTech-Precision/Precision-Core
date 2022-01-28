package gtwp;

import gtwp.blocks.GTWPBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import gtwp.metatileentities.GTWPMetaTileEntities;

@Mod(modid = GTWP.MODID,
        name = GTWP.NAME,
        version = GTWP.VERSION,
        dependencies = "required-after:gregtech")
public class GTWP {

    public GTWP(){}

    public static final String MODID = "gtwp";
    public static final String NAME = "GT:WP";
    public static final String VERSION = "0.0.1";

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        GTWPMetaTileEntities.init();
        GTWPBlocks.init();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {}

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {}
}
