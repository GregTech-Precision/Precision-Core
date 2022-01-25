package gtwp;

import gregtech.api.GregTechAPI;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import gtwp.machines.Entities.NewMetaTileEntities;

@SuppressWarnings("WeakerAccess")
@Mod(modid = GTWPMod.MODID,
        name = GTWPMod.NAME,
        version = GTWPMod.VERSION,
        dependencies = "required-after:gregtech")
public class GTWPMod {
    public static final String MODID = "gtwp";
    public static final String NAME = "GT:WP";
    public static final String VERSION = "0.0.1";

    public static void preInit(FMLInitializationEvent event)
    {
        NewMetaTileEntities.init();
    }

    private static ResourceLocation location(String name)
    {
        return new ResourceLocation(GTWPMod.MODID, name);
    }
}
