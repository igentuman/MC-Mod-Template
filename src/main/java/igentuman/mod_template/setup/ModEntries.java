package igentuman.mod_template.setup;

import igentuman.mod_template.registration.ModEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import java.util.HashMap;

import static igentuman.mod_template.registration.ModEntryBuilder.*;

public class ModEntries {
    public static final HashMap<String, ModEntry> ENTRIES = new HashMap<>();
    public static BlockBehaviour.Properties COMMON_BLOCK_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5f).requiresCorrectToolForDrops();

    public static final ModEntry EXAMPLE_ITEM = addItem("example_item").build();

    public static final ModEntry EXAMPLE_MACHINE = addProcessor("example_machine")
            .fluidCap(1,1)
            .itemCap(1,0)
            .build();

    public static final ModEntry OTHER_MACHINE = addProcessor("other_machine")
            .itemCap(1,1)
            .build();

    public static void init() {}

    public static ModEntry get(String name) {
        return ENTRIES.get(name);
    }
}
