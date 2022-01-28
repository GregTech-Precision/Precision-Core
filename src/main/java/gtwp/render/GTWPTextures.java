package gtwp.render;

import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.api.util.GTLog;
import gtwp.GTWP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GTWP.MODID, value = Side.CLIENT)
public class GTWPTextures {

    public static SimpleOverlayRenderer MULTI_TANK_FLUID_HATCH_T1;
    public static SimpleOverlayRenderer MULTI_TANK_FLUID_HATCH_T2;
    public static SimpleOverlayRenderer MULTI_TANK_FLUID_HATCH_T3;
    public static SimpleOverlayRenderer MULTI_TANK_FLUID_HATCH_T4;
    public static OrientedOverlayRenderer IO_HATCH_OVERLAY;

    public static void preInit()
    {
        GTLog.logger.info("GT:WP pre initializing textures");
        MULTI_TANK_FLUID_HATCH_T1 = new SimpleOverlayRenderer("multi_tank_fluid_storage_t1.png");
        MULTI_TANK_FLUID_HATCH_T2 = new SimpleOverlayRenderer("multi_tank_fluid_storage_t2.png");
        MULTI_TANK_FLUID_HATCH_T3 = new SimpleOverlayRenderer("multi_tank_fluid_storage_t3.png");
        MULTI_TANK_FLUID_HATCH_T4 = new SimpleOverlayRenderer("multi_tank_fluid_storage_t4.png");
        IO_HATCH_OVERLAY = new OrientedOverlayRenderer("IOHatch.png");
    }
}
