package gtwp.api.render;

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.api.util.GTLog;
import gtwp.GTWP;
import gtwp.common.blocks.GTWPMetaBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GTWP.MODID, value = Side.CLIENT)
public class GTWPTextures {

    public static SimpleOverlayRenderer[] MULTI_TANK_FLUID_STORAGE = new SimpleOverlayRenderer[4];
    public static SimpleOverlayRenderer[] IGLASS = new SimpleOverlayRenderer[16];
    public static SimpleOverlayRenderer PIPELINE;
    public static SimpleOverlayRenderer COMPUTER_CASING;
    public static SimpleOverlayRenderer SATELLITE_CASING;


    public static void preInit() {
        GTLog.logger.info("GT:WP pre initializing textures");
        for(int i = 1; i<=4;i++) MULTI_TANK_FLUID_STORAGE[i-1] = new SimpleOverlayRenderer("multi_tank/fluid_storage_t"+i);
        for(int i = 0; i<=15;i++) IGLASS[i] = new SimpleOverlayRenderer("iglass/iglass"+i);
        PIPELINE = new SimpleOverlayRenderer("blockpipeline/fluid");
        COMPUTER_CASING = new SimpleOverlayRenderer("parallel/computer_casing");
        SATELLITE_CASING = new SimpleOverlayRenderer("parallel/satellite_casing");
    }
}
