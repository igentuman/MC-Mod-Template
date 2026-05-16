package igentuman.mod_template.setup;

import igentuman.mod_template.multiblock.MultiblockEntry;
import igentuman.mod_template.multiblock.MultiblockEntryBuilder;
import igentuman.mod_template.multiblock.MultiblockRegistry;
import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.registration.ModEntryBuilder;
import igentuman.mod_template.util.SlotsLayout;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.awt.*;
import java.util.HashMap;

import static igentuman.mod_template.registration.ModEntryBuilder.*;
import static net.minecraft.world.level.block.Blocks.DIRT;
import static net.minecraft.world.level.block.Blocks.IRON_BLOCK;

public class ModEntries {
    public static final HashMap<String, ModEntry> ENTRIES = new HashMap<>();
    public static BlockBehaviour.Properties COMMON_BLOCK_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5f).requiresCorrectToolForDrops();

    public static final ModEntry EXAMPLE_ITEM = addItem("example_item").build();

    public static final ModEntry EXAMPLE_MACHINE = addProcessor("example_machine")
            .withLayout(SlotsLayout.THREE_TO_TWO)
            .fluidCap(2,2, 0)
            .itemCap(1,0)
            .build();

    public static final ModEntry SILVER = addMetalOreMaterial("silver", Color.LIGHT_GRAY.getRGB()).build();

    public static final ModEntry FOO_CONTROLLER = addMultiblockController("foo_controller")
            .withLayout(SlotsLayout.ONE_TO_ONE)
            .itemCap(1, 1)
            .withEnergyOutput(1_000_000_000)
            .build();

    public static final ModEntry FOO_PORT = addMultiblockPart("foo_port");

    public static final MultiblockEntry FOO_MULTIBLOCK = MultiblockEntryBuilder.name("foo_multiblock")
            .controller(FOO_CONTROLLER).ports(FOO_PORT)
            .casing(() -> SILVER.materialEntry().storageBlock().get(), () -> IRON_BLOCK)
            .sizeRange(3, 5, 3, 5, 3, 5)
            .interior(() -> DIRT)
            .build();

    public static void init() {}

    public static ModEntry get(String name) {
        return ENTRIES.getOrDefault(name, null);
    }
}
