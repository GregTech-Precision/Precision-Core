package precisioncore.common;

import gregtech.api.util.GTLog;
import gregtech.common.blocks.VariantItemBlock;
import precisioncore.PrecisionCore;
import precisioncore.common.blocks.PrecisionMetaBlocks;
import precisioncore.common.items.PrecisionMetaItems;
import precisioncore.loaders.recipes.PrecisionRecipeLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = PrecisionCore.MODID)
public class CommonProxy {

    public void preLoad() {
        PrecisionMetaItems.init();
//        GAMetaFluids.init();
    }

    @SubscribeEvent
    public static void syncConfigValues(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(PrecisionCore.MODID)) {
            ConfigManager.sync(PrecisionCore.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GTLog.logger.info("Registering blocks...");
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(PrecisionMetaBlocks.FLUID_STORAGE);
        registry.register(PrecisionMetaBlocks.IGLASS);
        registry.register(PrecisionMetaBlocks.PIPELINE);
        registry.register(PrecisionMetaBlocks.CASING);
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        GTLog.logger.info("Registering Items...");
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(createItemBlock(PrecisionMetaBlocks.FLUID_STORAGE, VariantItemBlock::new));
        registry.register(createItemBlock(PrecisionMetaBlocks.IGLASS, VariantItemBlock::new));
        registry.register(createItemBlock(PrecisionMetaBlocks.PIPELINE, VariantItemBlock::new));
        registry.register(createItemBlock(PrecisionMetaBlocks.CASING, VariantItemBlock::new));
        registry.register(createItemBlock(PrecisionMetaBlocks.PARALLEL_CASING, VariantItemBlock::new));
    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }

   @SubscribeEvent()
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        GTLog.logger.info("Registering recipe low...");
        PrecisionRecipeLoader.init();

       // Main recipe registration
        // This is called AFTER GregTech registers recipes, so
        // anything here is safe to call removals in
        //
    }

    @SubscribeEvent
    public static void registerOrePrefix(RegistryEvent.Register<IRecipe> event) {
//        GTLog.logger.info("Registering ore prefix...");

        // Register OreDictionary Entries
//        GAMetaItems.registerOreDict();
//        GAMetaBlocks.registerOreDict();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
//        RecipeHandler.runRecipeGeneration();
//        RecipeHandler.generatedRecipes();

    }
}
