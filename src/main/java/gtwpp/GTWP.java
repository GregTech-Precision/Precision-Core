package gtwpp;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import gtwpp.machines.Entities.NewMetaTileEntities;

@SuppressWarnings("WeakerAccess")
@Mod(modid = GTWP.MODID,
        name = GTWP.NAME,
        version = GTWP.VERSION,
        dependencies = "required-after:gregtech")
public class GTWP {
    public static final String MODID = "gtwp";
    public static final String NAME = "GT:WP";
    public static final String VERSION = "0.0.1";

    public static void preInit(FMLPreInitializationEvent event)
    {
        NewMetaTileEntities.init();
    }
}
