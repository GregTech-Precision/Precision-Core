package precisioncore.common;

import precisioncore.common.blocks.PrecisionMetaBlocks;
import precisioncore.api.render.PrecisionTextures;
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
        PrecisionTextures.preInit();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        GTLog.logger.info("Precision Core register item models");
        PrecisionMetaBlocks.registerItemModels();
    }
}