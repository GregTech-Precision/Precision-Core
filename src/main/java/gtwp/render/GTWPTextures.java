package gtwp.render;

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.api.util.GTLog;
import gtwp.GTWP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import static gregtech.client.renderer.texture.cube.OrientedOverlayRenderer.OverlayFace.*;

@Mod.EventBusSubscriber(modid = GTWP.MODID, value = Side.CLIENT)
public class GTWPTextures {

    public static SimpleOverlayRenderer[] MULTI_TANK_FLUID_STORAGE = new SimpleOverlayRenderer[4];

    public static void preInit() {
        GTLog.logger.info("GT:WP pre initializing textures");
        for(int i = 1; i<=4;i++) MULTI_TANK_FLUID_STORAGE[i-1] = new SimpleOverlayRenderer("multi_tank/fluid_storage_t"+i+".png");
    }
}
