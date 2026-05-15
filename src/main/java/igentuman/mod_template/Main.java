package igentuman.mod_template;

import igentuman.mod_template.block_entity.GlobalBlockEntity;
import igentuman.mod_template.config.Common;
import igentuman.mod_template.config.WorldGen;
import igentuman.mod_template.network.PacketAE2PatternTransfer;
import igentuman.mod_template.network.PacketSideConfigToggle;
import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import igentuman.mod_template.setup.Registers;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Main.MODID)
public class Main {
    public static final String MODID = "modtemplate";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Creates a creative tab with the id "modtemplate:example_tab" for the example item, that is placed after the combat tab
/*    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.modtemplate")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(EXAMPLE_MACHINE_BLOCK_ITEM.get());
            }).build());*/

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        Registers.init(modEventBus);
        ModEntries.init();

        NeoForge.EVENT_BUS.register(this);

        WorldGen.init(
                ModEntries.ENTRIES.values().stream()
                        .map(ModEntry::materialEntry)
                        .filter(mat -> mat != null && mat.hasWorldgenConfig())
                        .toList()
        );
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloads);
        modContainer.registerConfig(ModConfig.Type.COMMON, Common.SPEC, MODID + "/common.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, WorldGen.SPEC, MODID + "/worldgen.toml");
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("1");
        registrar.playToServer(
                PacketSideConfigToggle.TYPE,
                PacketSideConfigToggle.STREAM_CODEC,
                PacketSideConfigToggle::handle
        );
        registrar.playToServer(
                PacketAE2PatternTransfer.TYPE,
                PacketAE2PatternTransfer.STREAM_CODEC,
                PacketAE2PatternTransfer::handle
        );
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (entry.hasBlockEntity() && entry.itemCap() != null) {
                event.registerBlockEntity(
                        Capabilities.ItemHandler.BLOCK,
                        entry.blockEntity().get(),
                        (be, side) -> {
                            if (be instanceof GlobalBlockEntity gbe) {
                                return gbe.getItemHandler(side);
                            }
                            return null;
                        }
                );
            }
            if (entry.hasBlockEntity() && entry.fluidCap() != null) {
                event.registerBlockEntity(
                        Capabilities.FluidHandler.BLOCK,
                        entry.blockEntity().get(),
                        (be, side) -> {
                            if (be instanceof GlobalBlockEntity gbe) {
                                return gbe.getFluidHandler(side);
                            }
                            return null;
                        }
                );
            }
            if (entry.hasBlockEntity() && entry.energyCap() != null) {
                event.registerBlockEntity(
                        Capabilities.EnergyStorage.BLOCK,
                        entry.blockEntity().get(),
                        (be, side) -> {
                            if (be instanceof GlobalBlockEntity gbe) {
                                return gbe.getEnergyHandler(side);
                            }
                            return null;
                        }
                );
            }
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        for (ModEntry entry: ModEntries.ENTRIES.values()) {
            // Handle material entries (ores, ingots, blocks, etc.)
            if (entry.materialEntry() != null) {
                var mat = entry.materialEntry();
                if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                    if (mat.hasOre()) event.accept(mat.oreItem());
                    if (mat.hasRawOre()) event.accept(mat.rawOre());
                }
                if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                    if (mat.hasBlock()) event.accept(mat.storageItem());
                }
                if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                    if (mat.hasIngot()) event.accept(mat.ingot());
                    if (mat.hasGem()) event.accept(mat.gem());
                    if (mat.hasDust()) event.accept(mat.dust());
                    if (mat.hasPlate()) event.accept(mat.plate());
                    if (mat.hasNugget()) event.accept(mat.nugget());
                }
                if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                    if (mat.hasFluid()) event.accept(mat.bucket());
                }
                continue;
            }
            if(entry.hasBlockEntity()) {
                if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                    event.accept(entry.item());
                }
                continue;
            }
            if(entry.hasBlock()) {
                if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                    event.accept(entry.item());
                }
                continue;
            }
            if(entry.hasItem()) {
                if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                    event.accept(entry.item());
                }
                continue;
            }
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation rlFromString(String name) {
        return ResourceLocation.tryParse(name);
    }
}
