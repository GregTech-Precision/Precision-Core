package precisioncore;

import precisioncore.common.CommonProxy;
import precisioncore.common.blocks.PrecisionMetaBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import precisioncore.common.metatileentities.PrecisionMetaTileEntities;

@Mod(modid = PrecisionCore.MODID,
        name = PrecisionCore.NAME,
        version = PrecisionCore.VERSION,
        dependencies = "required-after:gregtech")
public class PrecisionCore {

    public PrecisionCore(){}

    public static final String MODID = "precisioncore";
    public static final String NAME = "Precision Core";
    public static final String VERSION = "0.0.5";

    @SidedProxy(modId = MODID, clientSide = "precisioncore.common.ClientProxy", serverSide = "precisioncore.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {


        PrecisionMetaBlocks.init();
        PrecisionMetaTileEntities.init();

        proxy.preLoad();
    }
}
