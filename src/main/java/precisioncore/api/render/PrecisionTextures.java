package precisioncore.api.render;

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.api.util.GTLog;
import precisioncore.PrecisionCore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = PrecisionCore.MODID, value = Side.CLIENT)
public class PrecisionTextures {

    public static SimpleOverlayRenderer[] MULTI_TANK_FLUID_STORAGE = new SimpleOverlayRenderer[4];
    public static SimpleOverlayRenderer[] IGLASS = new SimpleOverlayRenderer[16];
    public static SimpleOverlayRenderer PIPELINE;
    public static SimpleOverlayRenderer COMPUTER_CASING;
    public static SimpleOverlayRenderer SATELLITE_CASING;
    public static SimpleOverlayRenderer PARALLEL_HATCH_GREEN;
    public static SimpleOverlayRenderer PARALLEL_HATCH_YELLOW;
    public static SimpleOverlayRenderer PARALLEL_HATCH_RED;
    public static SimpleOverlayRenderer PARALLEL_RACK_EMPTY;
    public static SimpleOverlayRenderer PARALLEL_RACK_ACTIVE;
    public static SimpleOverlayRenderer PARALLEL_RACK_INACTIVE;
    public static SimpleOverlayRenderer CASING_ME;
    public static SimpleOverlayRenderer AE_CONNECTOR_ACTIVE;
    public static SimpleOverlayRenderer AE_CONNECTOR_INACTIVE;



    public static void preInit() {
        GTLog.logger.info("GT:WP pre initializing textures");
        for(int i = 1; i<=4;i++) MULTI_TANK_FLUID_STORAGE[i-1] = new SimpleOverlayRenderer("multi_tank/fluid_storage_t"+i);
        for(int i = 0; i<=15;i++) IGLASS[i] = new SimpleOverlayRenderer("iglass/iglass"+i);
        PIPELINE = new SimpleOverlayRenderer("blockpipeline/fluid");
        COMPUTER_CASING = new SimpleOverlayRenderer("parallel/casing_computer");
        SATELLITE_CASING = new SimpleOverlayRenderer("parallel/casing_satellite");
        PARALLEL_HATCH_GREEN = new SimpleOverlayRenderer("parallel/hatch_green");
        PARALLEL_HATCH_YELLOW = new SimpleOverlayRenderer("parallel/hatch_yellow");
        PARALLEL_HATCH_RED = new SimpleOverlayRenderer("parallel/hatch_red");
        PARALLEL_RACK_EMPTY = new SimpleOverlayRenderer("parallel/rack_overlay_empty");
        PARALLEL_RACK_ACTIVE = new SimpleOverlayRenderer("parallel/rack_overlay_active");
        PARALLEL_RACK_INACTIVE = new SimpleOverlayRenderer("parallel/rack_overlay_inactive");
        CASING_ME = new SimpleOverlayRenderer("matrixsystem/casing_me");
        AE_CONNECTOR_ACTIVE = new SimpleOverlayRenderer("matrixsystem/ae_connector_active");
        AE_CONNECTOR_INACTIVE = new SimpleOverlayRenderer("matrixsystem/ae_connector_inactive");
    }
}
