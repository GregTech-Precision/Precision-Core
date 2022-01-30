package gtwp;

import gtwp.blocks.GTWPMetaBlocks;
import gtwp.render.GTWPTextures;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import gregtech.api.util.GTLog;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preLoad() {
        super.preLoad();
        GTWPTextures.preInit();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        GTLog.logger.info("GT:WP register item models");
        GTWPMetaBlocks.registerItemModels();
    }
}