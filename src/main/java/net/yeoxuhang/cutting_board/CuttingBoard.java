package net.yeoxuhang.cutting_board;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yeoxuhang.cutting_board.block.CuttingBoardBlock;
import net.yeoxuhang.cutting_board.block_entity.CuttingBoardBlockEntity;
import net.yeoxuhang.cutting_board.client.CuttingBoardBlockEntityRenderer;
import org.slf4j.Logger;
@Mod(CuttingBoard.MODID)
public class CuttingBoard {

    //This is the mod id for registration
    public static final String MODID = "cutting_board";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Create cutting board block
    public static final RegistryObject<CuttingBoardBlock> CUTTING_BOARD = BLOCKS.register("cutting_board", () -> new CuttingBoardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)));
    // Create cutting board block entity
    public static final RegistryObject<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD_BLOCK_ENTITY =
            BLOCK_ENTITY.register("cutting_board", () ->
                    BlockEntityType.Builder.of(CuttingBoardBlockEntity::new,
                            CUTTING_BOARD.get()).build(null));
    // Create cutting board block item
    public static final RegistryObject<Item> CUTTING_BOARD_ITEM = ITEMS.register("cutting_board", () -> new BlockItem(CUTTING_BOARD.get(), new Item.Properties()));

    // Create knife for cutting cake
    public static final RegistryObject<Item> AMETHYST_KNIFE = ITEMS.register("amethyst_knife", () -> new SwordItem(Tiers.IRON, 1, -3.6F, new Item.Properties().durability(50)));

    // Create food properties for cake slice (You can make different class for this one or make sure it put before your food item field)
    public static final FoodProperties CAKE_SLICE_PROPERTIES = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
    // Create cake slice for cut cakes
    public static final RegistryObject<Item> CAKE_SLICE = ITEMS.register("cake_slice", ()-> new Item(new Item.Properties().food(CAKE_SLICE_PROPERTIES)));

    // Create creative mode tab for the mod
    public static final RegistryObject<CreativeModeTab> CUTTING_AND_CHOPPING = CREATIVE_MODE_TABS.register("cutting_and_chopping", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> AMETHYST_KNIFE.get().getDefaultInstance()).title(Component.translatable("itemGroup.cutting_board.cutting_and_chopping")).displayItems((parameters, output) -> {
        output.accept(AMETHYST_KNIFE.get());
        output.accept(CUTTING_BOARD_ITEM.get());
        output.accept(CAKE_SLICE.get());
    }).build());

    // This is a tag, which can be found in data/mod-id/tags/(items or blocks, entities)
    // Different tag type has its own way, like TagKey<Item> for items, TagKey<Block> for blocks, TagKey<Entity> for entities, TagKey<Biomes> for biomes
    public static final TagKey<Item> FOOD = bind("can_be_place_on_cutting_board");

    private static TagKey<Item> bind(String name) {
        // When changing the tag type, make sure change the ITEM to something else
        return TagKey.create(Registries.ITEM, new ResourceLocation(MODID, name));
    }

    public CuttingBoard() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so block entity get registered
        BLOCK_ENTITY.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Register cutting board renderer
            BlockEntityRenderers.register(CUTTING_BOARD_BLOCK_ENTITY.get(), CuttingBoardBlockEntityRenderer::new);
        }
    }
}
