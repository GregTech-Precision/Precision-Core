package gtwpp;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import gtwpp.machines.Entities.NewMetaTileEntities;

@Mod(modid = GTWP.MODID, name = GTWP.NAME, version = GTWP.VERSION, dependencies = "required-after:gregtech")
public class GTWP
{
    public static final String MODID = "gtwp";
    public static final String NAME = "GT:WP";
    public static final String VERSION = "0.0.1";

    @EventHandler
    static void onInit(FMLInitializationEvent event)
    {
        NewMetaTileEntities.init();
    }
}
